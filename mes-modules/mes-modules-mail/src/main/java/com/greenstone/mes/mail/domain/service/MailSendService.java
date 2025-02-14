package com.greenstone.mes.mail.domain.service;

import com.greenstone.mes.mail.domain.entity.MailFinal;

import javax.mail.internet.MimeMessage;

public interface MailSendService {

    void sendMessage(MimeMessage mimeMessage, String username);

    MimeMessage buildMimeMessage(MailFinal mail);

}
