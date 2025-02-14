package com.greenstone.mes.mail.domain.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Data
public class MailStoreManager {

    public static final String DEFAULT_PROTOCOL = "imap";

    public static final int DEFAULT_PORT = 993;

    private static final String HEADER_MESSAGE_ID = "Message-ID";

    private static final String FOLDER_SENT = "Sent";

    private Properties storeProperties = new Properties();

    private Session session;

    @Nullable
    private String protocol;

    @Nullable
    private String username;

    @Nullable
    private String password;

    @Nullable
    private String host;

    @Nullable
    private String folderName;

    private int port = DEFAULT_PORT;

    public void setStoreProperties(Properties storeProperties) {
        this.storeProperties = storeProperties;
        synchronized (this) {
            this.session = null;
        }
    }

    public void store(MimeMessage mimeMessage, String folderName) {
        this.setFolderName(folderName);
        store(mimeMessage);
    }

    /**
     * 保存邮件到邮箱，默认保存到"已发送"文件夹
     *
     * @param mimeMessage mimeMessage
     * @throws MailException MailException
     */
    public void store(MimeMessage mimeMessage) throws MailException {
        Store store = null;
        Folder folder = null;
        try {
            try {
                store = connectStore();
            } catch (AuthenticationFailedException ex) {
                throw new MailAuthenticationException(ex);
            } catch (Exception ex) {
                throw new MailSendException("Mail server connection failed", ex);
            }

            try {
                if (mimeMessage.getSentDate() == null) {
                    mimeMessage.setSentDate(new Date());
                }
                String messageId = mimeMessage.getMessageID();
                if (messageId != null) {
                    // 显式指定消息id
                    mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
                }

                folder = openFolder(store);
                folder.appendMessages(new Message[]{mimeMessage});

            } catch (MessagingException e) {
                throw new MailSendException("Mail store append message failed", e);
            }
        } finally {
            if (folder != null) {
                try {
                    folder.close(false);
                } catch (MessagingException e) {
                    log.error("Close folder failed", e);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    log.error("Close store failed", e);
                }
            }
        }
    }

    public synchronized Session getSession() {
        if (this.session == null) {
            this.session = Session.getInstance(getStoreProperties());
        }
        return this.session;
    }

    public Folder openFolder(Store store) throws MessagingException {
        String folderName = getFolderName();
        if (folderName == null) {
            folderName = FOLDER_SENT;
        }
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        return folder;
    }

    protected Store connectStore() throws MessagingException {
        String username = getUsername();
        String password = getPassword();
        if ("".equals(username)) {
            username = null;
            if ("".equals(password)) {
                password = null;
            }
        }

        Store store = getStore(getSession());
        store.connect(getHost(), getPort(), username, password);
        return store;
    }


    protected Store getStore(Session session) throws NoSuchProviderException {
        String protocol = getProtocol();
        if (protocol == null) {
            protocol = session.getProperty("mail.store.protocol");
            if (protocol == null) {
                protocol = DEFAULT_PROTOCOL;
            }
        }
        return session.getStore(protocol);
    }

}
