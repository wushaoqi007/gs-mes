package com.greenstone.mes.mail.domain.service;

import javax.mail.internet.MimeMessage;

public interface MailStoreService {

    void storeSentMessage(MimeMessage mimeMessage, String username);

    void storeMessage(MimeMessage mimeMessage, String username, String folder);

}
