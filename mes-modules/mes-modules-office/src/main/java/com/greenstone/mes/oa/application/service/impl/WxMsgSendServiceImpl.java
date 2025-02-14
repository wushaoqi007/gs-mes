package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.enums.WxError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.assembler.WxMsgAssembler;
import com.greenstone.mes.oa.application.service.WxMsgSendService;
import com.greenstone.mes.oa.domain.entity.WxMessage;
import com.greenstone.mes.oa.domain.repository.WxMessageRepository;
import com.greenstone.mes.oa.enums.WxMsgType;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMessageService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2022-11-01-15:08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WxMsgSendServiceImpl implements WxMsgSendService {

    private final WxConfig wxConfig;
    private final WxcpService externalWxService;
    private final RemoteUserService userService;
    private final WxMsgAssembler msgAssembler;
    private final WxMessageRepository messageRepository;

    @Override
    public void sendMsgToWx(WxMsgSendCmd msgSendCmd) {
        log.info(JSON.toJSONString(msgSendCmd));
        msgSendCmd = defaultCheck(msgSendCmd);
        WxCpService cpService = externalWxService.getWxCpService(msgSendCmd.getCpId(), msgSendCmd.getAgentId());
        WxCpMessageService messageService = cpService.getMessageService();
        List<WxCpMessage> messageList;
        if (WxMsgType.TEXT_CARD.equals(msgSendCmd.getMsgType())) {
            messageList = assembleTextCard(msgSendCmd);
        } else {
            messageList = assembleText(msgSendCmd);
        }
        try {
            for (WxCpMessage message : messageList) {
                log.info("wx msg notice send:{}", message);
                WxCpMessageSendResult sendResult = messageService.send(message);
                log.info("wx msg notice result:{}", sendResult);
            }
            // 记录已发送的企业微信信息
            WxMessage wxMessage = msgAssembler.toWxMessage(msgSendCmd);
            messageRepository.add(wxMessage);
        } catch (WxErrorException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 组装文本类微信消息
     */
    public List<WxCpMessage> assembleText(WxMsgSendCmd msgSendCmd) {
        List<WxCpMessage> messageList = new ArrayList<>();
        for (WxMsgSendCmd.WxMsgUser user : msgSendCmd.getToUser()) {
            WxCpMessage message = new WxCpMessage();
            message.setToUser(user.getWxUserId());
            message.setMsgType(msgSendCmd.getMsgType().getType());
            message.setAgentId(msgSendCmd.getAgentId());
            message.setContent(msgSendCmd.getContent());
            messageList.add(message);
        }
        return messageList;
    }

    /**
     * 组装文本卡片类微信消息
     */
    public List<WxCpMessage> assembleTextCard(WxMsgSendCmd msgSendCmd) {
        List<WxCpMessage> messageList = new ArrayList<>();
        for (WxMsgSendCmd.WxMsgUser user : msgSendCmd.getToUser()) {
            WxCpMessage message = new WxCpMessage();
            message.setToUser(user.getWxUserId());
            // 部门信息
//        message.setToParty(deptId);
            message.setMsgType(msgSendCmd.getMsgType().getType());
            message.setAgentId(msgSendCmd.getAgentId());
            message.setTitle(StrUtil.isEmpty(msgSendCmd.getTitle()) ? "系统提醒" : msgSendCmd.getTitle());
            message.setDescription(StrUtil.isEmpty(msgSendCmd.getContent()) ? "格林司通管理系统已上线，欢迎访问" : msgSendCmd.getContent());
            message.setUrl(StrUtil.isEmpty(msgSendCmd.getUrl()) ? wxConfig.getOauth2RedirectUri() : msgSendCmd.getUrl());
            message.setEnableIdTrans(false);
            message.setEnableDuplicateCheck(false);
            message.setDuplicateCheckInterval(1800);
            messageList.add(message);
        }
        return messageList;
    }

    public WxMsgSendCmd defaultCheck(WxMsgSendCmd msgSendCmd) {
        // 默认使用格林司通自动化企业微信发送
        if (StrUtil.isEmpty(msgSendCmd.getCpId())) {
            msgSendCmd.setCpId(WxCp.AUTOMATION.getCpId());
        }

        if (CollUtil.isEmpty(msgSendCmd.getToUser())) {
            log.error("msg send fail because wx user is empty! msg:{}", msgSendCmd);
            throw new ServiceException(WxError.E70102, msgSendCmd.toString());
        }
        for (WxMsgSendCmd.WxMsgUser user : msgSendCmd.getToUser()) {
            if (StrUtil.isEmpty(user.getWxUserId())) {
                SysUser sysUser = userService.getUser(SysUser.builder().userId(user.getSysUserId()).build());
                if (Objects.isNull(sysUser)) {
                    log.error("msg send fail because not find wx user,system user id:{}", user);
                    throw new ServiceException(WxError.E70101, user.toString());
                }
                if (StrUtil.isEmpty(sysUser.getWxUserId())) {
                    log.error("msg send fail because not find wx user id,system user:{}", sysUser);
                    throw new ServiceException(WxError.E70103, sysUser.toString());
                }
                user.setWxUserId(sysUser.getWxUserId());
            }
        }
        // 默认使用格林司通系统自建应用发送
        if (Objects.isNull(msgSendCmd.getAgentId())) {
            msgSendCmd.setAgentId(wxConfig.getAgentId(WxConfig.SYSTEM));
        }
        // 默认发送文本消息
        if (Objects.isNull(msgSendCmd.getMsgType())) {
            msgSendCmd.setMsgType(WxMsgType.TEXT);
        }
        return msgSendCmd;
    }
}
