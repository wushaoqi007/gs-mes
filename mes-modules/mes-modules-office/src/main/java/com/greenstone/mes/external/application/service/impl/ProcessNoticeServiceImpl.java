package com.greenstone.mes.external.application.service.impl;

import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.external.application.dto.cmd.FlowNoticeCmd;
import com.greenstone.mes.external.application.service.ProcessNoticeService;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.enums.MsgCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProcessNoticeServiceImpl implements ProcessNoticeService {

    private final RemoteSystemService systemService;
    private final RemoteOaService oaService;
    private final RemoteMailService mailService;


    @Override
    public void sendNotice(FlowNoticeCmd noticeCmd, List<SysUser> users) {
        switch (noticeCmd.getWay()) {
            case EMAIL -> {
                List<MailAddress> mailAddresses = users.stream().map(user -> new MailAddress(user.getEmail(), user.getNickName())).toList();
                MailSendCmd sendCmd = MailSendCmd.builder()
                        .businessKey("process_notice")
                        .serialNo(noticeCmd.getSerialNo())
                        .subject(noticeCmd.getTitle())
                        .content(noticeCmd.getContent())
                        .to(mailAddresses).build();
                mailService.sendAsync(sendCmd);
            }
            case SYS_MESSAGE -> {
                List<Long> userIds = users.stream().map(SysUser::getUserId).toList();
                MessageSaveCmd saveCmd = MessageSaveCmd.builder()
                        .recipientIds(userIds)
                        .sourceId(noticeCmd.getSerialNo())
                        .category(MsgCategory.ADMIN_NOTICE)
                        .title(noticeCmd.getTitle())
                        .subTitle(noticeCmd.getSubTitle())
                        .content(noticeCmd.getContent()).build();
                systemService.sendSysMsg(saveCmd);
            }
            case WX_WORK_MSG -> {
                List<WxMsgSendCmd.WxMsgUser> userIds = users.stream().map(u -> WxMsgSendCmd.WxMsgUser.builder().sysUserId(u.getUserId()).build()).toList();
                WxMsgSendCmd msgSendCmd = WxMsgSendCmd.builder()
                        .toUser(userIds)
                        .content(noticeCmd.getContent()).build();
                oaService.sendMsgToWx(msgSendCmd);
            }
        }
    }

}
