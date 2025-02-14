package com.greenstone.mes.mail.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(contextId = "remoteMailService", value = ServiceNameConstants.MAIL_SERVICE)
public interface RemoteMailService {

    /**
     * 发送邮件
     *
     * @param mailSendCmd 邮件发送内容
     * @return 邮件发送结果
     */
    @PostMapping("/mail/send")
    MailSendResult send(@RequestBody MailSendCmd mailSendCmd);

    /**
     * 异步发送邮件，发送结束后会通过mq发送结果
     *
     * @param mailSendCmd 邮件发送内容
     */
    @PostMapping("/mail/sendAsync")
    void sendAsync(@RequestBody MailSendCmd mailSendCmd);

}
