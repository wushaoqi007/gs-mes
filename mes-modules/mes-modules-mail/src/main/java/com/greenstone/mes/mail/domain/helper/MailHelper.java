package com.greenstone.mes.mail.domain.helper;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.mail.domain.entity.MailSimple;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailHelper {

    private final RemoteUserService userService;

    public MailSimple buildMailSimple(MailSendCmd mailSendCmd) {
        MailSimple mailSimple = MailSimple.builder().businessKey(mailSendCmd.getBusinessKey())
                .serialNo(mailSendCmd.getSerialNo())
                .sender(mailSendCmd.getSender())
                .subject(mailSendCmd.getSubject())
                .content(mailSendCmd.getContent())
                .html(mailSendCmd.isHtml())
                .to(mailSendCmd.getTo())
                .cc(mailSendCmd.getCc())
                .inLines(mailSendCmd.getInLines())
                .attachments(mailSendCmd.getAttachments()).build();

        if (CollUtil.isNotEmpty(mailSendCmd.getToUserIds())) {
            for (Long userId : mailSendCmd.getToUserIds()) {
                SysUser user = userService.getUser(SysUser.builder().userId(userId).build());
                if (user != null) {
                    mailSimple.getTo().add(MailAddress.builder().address(user.getEmail()).personal(user.getNickName()).build());
                } else {
                    mailSimple.appendErrorMsg("收件人id不存在：" + userId);
                }
            }
        }

        if (CollUtil.isNotEmpty(mailSendCmd.getCcUserIds())) {
            for (Long userId : mailSendCmd.getCcUserIds()) {
                SysUser user = userService.getUser(SysUser.builder().userId(userId).build());
                if (user != null) {
                    mailSimple.getCc().add(MailAddress.builder().address(user.getEmail()).personal(user.getNickName()).build());
                } else {
                    mailSimple.appendErrorMsg("抄送人id不存在：" + userId);
                }
            }
        }

        return mailSimple;
    }

}
