package com.greenstone.mes.mail.domain.service.impl;

import com.greenstone.mes.mail.domain.entity.MailStoreManager;
import com.greenstone.mes.mail.domain.service.MailStoreService;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailStoreServiceImpl implements MailStoreService {


    private static final String SENT_FOLDER = "Sent";

    private final MailConfig mailConfig;

    @Override
    public void storeSentMessage(MimeMessage mimeMessage, String username) {
        storeMessage(mimeMessage, username, SENT_FOLDER);
    }

    @Override
    public void storeMessage(MimeMessage mimeMessage, String username, String folder) {
        MailStoreManager mailStoreManager = getMailStoreManager(username, folder);
        mailStoreManager.store(mimeMessage);
    }

    private final Map<String, MailStoreManager> storeMap = new HashMap<>();

    private MailStoreManager getMailStoreManager(String username, String folderName) {
        MailConfig.Mailbox mailbox = mailConfig.getMailbox(username);
        return getMailStoreManager(createProps(), mailbox.getHost(), mailbox.getUsername(), mailbox.getPassword(), folderName);
    }

    private MailStoreManager getMailStoreManager(Properties props, String host, String username, String password, String folderName) {
        return storeMap.computeIfAbsent(username, new Function<String, MailStoreManager>() {
            @Override
            public MailStoreManager apply(String username) {
                MailStoreManager storeManager = new MailStoreManager();
                storeManager.setStoreProperties(props);
                storeManager.setHost(host);
                storeManager.setUsername(username);
                storeManager.setPassword(password);
                storeManager.setFolderName(folderName);
                return storeManager;
            }
        });
    }

    private Properties createProps() {
        Properties storeProps = System.getProperties();
        storeProps.setProperty("mail.store.protocol", "imap");
        storeProps.setProperty("mail.imap.host", mailConfig.getServer().getHost());
        storeProps.setProperty("mail.imap.auth", "true");
        storeProps.setProperty("mail.imap.port", "993");
        storeProps.setProperty("mail.imap.starttls.enable", "true");
        storeProps.setProperty("mail.smtp.ssl.enable", "true");
        storeProps.setProperty("mail.imap.socketFactory.port", "993");
        storeProps.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        storeProps.setProperty("mail.imap.socketFactory.fallback", "false");
        return storeProps;
    }

}
