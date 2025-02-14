package com.greenstone.mes.mail.domain.service;

import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.mail.domain.entity.MailSimple;

public interface MailService {

    MailSendResult send(MailSimple mailSimple);

    void sendAsync(MailSimple mailSimple);

    MailSendResult resend(Long mailId);

    void resendAsync(Long mailId);

    void getFolders();

    void searchMails();
}
