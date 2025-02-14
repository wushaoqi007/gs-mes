package com.greenstone.mes.mail.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.mail.domain.entity.MailSimple;
import com.greenstone.mes.mail.domain.helper.MailHelper;
import com.greenstone.mes.mail.domain.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailApi {

    private final MailHelper mailHelper;
    private final MailService mailService;

    @PostMapping("/send")
    public AjaxResult send(@RequestBody @Validated MailSendCmd mailSendCmd) {
        MailSimple mailSimple = mailHelper.buildMailSimple(mailSendCmd);
        MailSendResult sendResult = mailService.send(mailSimple);
        return AjaxResult.success(sendResult);
    }

    @ApiLog
    @PostMapping("/sendAsync")
    public AjaxResult sendAsync(@RequestBody @Validated MailSendCmd mailSendCmd) {
        MailSimple mailSimple = mailHelper.buildMailSimple(mailSendCmd);
        mailService.sendAsync(mailSimple);
        return AjaxResult.success();
    }

    @PostMapping("/resend")
    public AjaxResult resend(@RequestBody MailSendCmd mailSendCmd) {
        if (mailSendCmd.getMailId() == null) {
            throw new RuntimeException("请指定邮件id");
        }
        return AjaxResult.success();
    }


}
