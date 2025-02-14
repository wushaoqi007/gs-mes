package com.greenstone.mes.table.domain.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.adapter.UserServiceAdapter;
import com.greenstone.mes.table.domain.entity.NoticeData;
import com.greenstone.mes.table.domain.service.ItemNoticeService;
import com.greenstone.mes.table.infrastructure.config.LinkConfig;
import com.greenstone.mes.table.infrastructure.mapper.ItemNoticeMapper;
import com.greenstone.mes.table.infrastructure.persistence.ItemNotice;
import com.greenstone.mes.wxcp.domain.helper.WxMsgService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemNoticeServiceImpl implements ItemNoticeService {

    private final ItemNoticeMapper itemNoticeMapper;
    private final RemoteMailService mailService;
    private final WxMsgService wxMsgService;
    private final UserServiceAdapter userServiceAdapter;
    private final WxConfig wxConfig;
    private final LinkConfig linkConfig;

    private final TimedCache<Long, ItemNotice> noticeCache = CacheUtil.newTimedCache(2 * 60 * 60 * 1000);

    @Override
    public void sendNotice(NoticeData noticeData) {
        if (noticeData.getItemId() == null || noticeData.getItemAction() == null || noticeData.getActionUser() == null || noticeData.getFunctionId() == null) {
            throw new RuntimeException("通知失败：缺少参数");
        }
        ItemNotice itemNotice = getNoticeConfig(noticeData.getFunctionId());
        if (itemNotice == null || !itemNotice.getItemActions().contains(noticeData.getItemAction())) {
            log.debug("事件通知：没有匹配的通知：{}", JSONObject.toJSONString(noticeData));
            return;
        }

        List<Long> emailSendUserIds = new ArrayList<>();
        List<Long> emailCopyUserIds = new ArrayList<>();
        List<Long> wxMsgUserIds = new ArrayList<>();

        if (noticeData.getEmailSendUserIds() != null) {
            emailSendUserIds.addAll(noticeData.getEmailSendUserIds());
        }
        if (itemNotice.getEmailSendUsers() != null) {
            emailSendUserIds.addAll(itemNotice.getWxMsgUsers());
        }
        if (noticeData.getEmailCopyUserIds() != null) {
            emailCopyUserIds.addAll(noticeData.getEmailCopyUserIds());
        }
        if (itemNotice.getEmailCopyUsers() != null) {
            emailCopyUserIds.addAll(itemNotice.getEmailCopyUsers());
        }
        if (noticeData.getWxMsgUserIds() != null) {
            wxMsgUserIds.addAll(noticeData.getWxMsgUserIds());
        }
        if (itemNotice.getWxMsgUsers() != null) {
            wxMsgUserIds.addAll(itemNotice.getWxMsgUsers());
        }

        // 发送邮件
        if (CollUtil.isNotEmpty(emailSendUserIds) || CollUtil.isNotEmpty(itemNotice.getPredefineEmails())) {
            sendMail(noticeData, itemNotice, emailSendUserIds, emailCopyUserIds);
        }

        // 发送微信消息
        if (CollUtil.isNotEmpty(wxMsgUserIds)) {
            sendWxMsg(noticeData, wxMsgUserIds);
        }

    }

    private ItemNotice getNoticeConfig(Long functionId) {
        ItemNotice itemNotice = noticeCache.get(functionId);
        if (itemNotice == null) {
            synchronized (this) {
                itemNotice = noticeCache.get(functionId);
                if (itemNotice == null) {
                    LambdaQueryWrapper<ItemNotice> wrapper = Wrappers.lambdaQuery(ItemNotice.class);
                    wrapper.eq(ItemNotice::getFunctionId, functionId);
                    itemNotice = itemNoticeMapper.selectOne(wrapper);
                    // TODO
                }
            }
        }
        return itemNotice;
    }

    private void sendMail(NoticeData noticeData, ItemNotice itemNotice, List<Long> emailSendUserIds, List<Long> emailCopyUserIds) {
        String notice = StrUtil.format("{}{}了{}: {}", noticeData.getActionUser().getNickName(), getActionName(noticeData.getItemAction()),
                noticeData.getFunctionName(), noticeData.getSerialNo());
        String url = linkConfig.getDetailLink(noticeData.getFunctionId(), noticeData.getItemId());
        String html = StrUtil.format(getMailHtmlTemplate(), notice, url);
        MailSendCmd sendCmd = MailSendCmd.builder().businessKey(String.valueOf(noticeData.getFunctionId()))
                .serialNo(String.valueOf(noticeData.getItemId()))
                .toUserIds(emailSendUserIds)
                .cc(itemNotice.getPredefineEmails().stream().map(e -> MailAddress.builder().address(e).build()).toList())
                .ccUserIds(emailCopyUserIds)
                .subject(notice)
                .content(html)
                .html(true).build();
        mailService.sendAsync(sendCmd);
    }

    private void sendWxMsg(NoticeData noticeData, List<Long> wxMsgUserIds) {
        List<String> wxUserIds = wxMsgUserIds.stream().map(userServiceAdapter::getUserById).map(User::getWxUserId).toList();
        String wxUserIdsStr = CollUtil.join(wxUserIds, "|");
        String title = StrUtil.format("{}{}了{}: {}", noticeData.getActionUser().getNickName(), getActionName(noticeData.getItemAction()),
                noticeData.getFunctionName(), noticeData.getSerialNo());
        String url = linkConfig.getDetailLink(noticeData.getFunctionId(), noticeData.getItemId());
        WxCpMessage message = WxCpMessage.TEXTCARD().toUser(wxUserIdsStr).url(url).title("消息通知").btnTxt("查看详情").description(title).build();
        wxMsgService.sendMsg(new CpId(wxConfig.getDefaultCpId()), wxConfig.getDefaultAgentId(), message);
    }

    private String getActionName(String action) {
        return switch (action) {
            case "create" -> "创建";
            case "update" -> "更新";
            case "delete" -> "删除";
            case "lock" -> "锁定";
            case "unlock" -> "解锁";
            case "export" -> "导出";
            case "import" -> "导入";
            default -> "操作";
        };
    }

    private String getMailHtmlTemplate() {
        return """
                <!DOCTYPE html> \s
                <html lang="zh-CN"> \s
                <head> \s
                    <meta charset="UTF-8"> \s
                    <meta name="viewport" content="width=device-width, initial-scale=1.0"> \s
                    <title>消息通知</title> \s
                    <style> \s
                        body { \s
                            font-family: Arial, sans-serif; \s
                            line-height: 1.6; \s
                            margin: 0; \s
                            padding: 20px; \s
                            background-color: #f4f4f4; \s
                            color: #333; \s
                        } \s
                        .container { \s
                            max-width: 600px; \s
                            margin: auto; \s
                            background: #fff; \s
                            padding: 20px; \s
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); \s
                        } \s
                        h1 { \s
                            margin-top: 0; \s
                            font-size: 24px; \s
                            color: #555; \s
                        } \s
                        p { \s
                            margin: 10px 0; \s
                        } \s
                        a { \s
                            color: #1e90ff; \s
                            text-decoration: none; \s
                        } \s
                        a:hover { \s
                            text-decoration: underline; \s
                        } \s
                    </style> \s
                </head> \s
                <body> \s
                    <div class="container"> \s
                        <h1>消息通知</h1> \s
                        <p>{}。</p> \s
                        <p><a href="{}" target="_blank">点击查看</a></p> \s
                    </div> \s
                </body> \s
                </html>
                """;
    }

}
