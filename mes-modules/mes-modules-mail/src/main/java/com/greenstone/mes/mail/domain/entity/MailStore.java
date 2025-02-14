package com.greenstone.mes.mail.domain.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.core.store.StoreSupport;
import org.springframework.lang.Nullable;

import javax.mail.*;
import java.util.Properties;

@Slf4j
@Data
public class MailStore {

    public static final String DEFAULT_PROTOCOL = "imap";

    public static final int DEFAULT_PORT = 993;

    private static final String HEADER_MESSAGE_ID = "Message-ID";

    private Properties storeProperties;

    private Session session;

    private Store store;

    @Nullable
    private String protocol;

    private String username;

    private String password;

    @Nullable
    private String host;

    private int port;

    private MailStore() {
    }

    public static MailStore newInstance(String host, String port, String username, String password) {
        MailStore mailStore = new MailStore();
        mailStore.setUsername(username);
        mailStore.setPassword(password);
        mailStore.setHost(host);
        mailStore.setPort(Integer.parseInt(port));
        mailStore.setStoreProperties(createProps(host, port));
        return mailStore;
    }

    public void getFolders() {
        Store store = getStore();
        Folder[] folders;
        try {
            folders = store.getDefaultFolder().list();
            for (Folder folder : folders) {
                System.out.println(folder.getFullName());
                Folder[] list = folder.list();
                if (list != null) {
                    for (Folder f : list) {
                        System.out.println(f.getFullName());
                    }
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Store getStore() {
        if (this.store == null) {
            String protocol = getProtocol();
            if (protocol == null) {
                protocol = getSession().getProperty("mail.store.protocol");
                if (protocol == null) {
                    protocol = DEFAULT_PROTOCOL;
                }
            }
            Store store;
            try {
                store = getSession().getStore(protocol);
            } catch (NoSuchProviderException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("连接邮箱失败，请联系管理员。");
            }
            try {
                store.connect(getHost(), getPort(), getUsername(), getPassword());
            } catch (MessagingException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("连接邮箱失败，请检查邮箱地址和密码是否正确。");
            }
            this.store = store;
        }
        return this.store;
    }

    private Session getSession() {
        if (this.session == null) {
            this.session = Session.getInstance(getStoreProperties());
        }
        return this.session;

    }

    private static Properties createProps(String host, String port) {
        Properties storeProps = System.getProperties();
        storeProps.setProperty("mail.store.protocol", "imap");
        storeProps.setProperty("mail.imap.host", host);
        storeProps.setProperty("mail.imap.auth", "true");
        storeProps.setProperty("mail.imap.port", port);
        storeProps.setProperty("mail.imap.starttls.enable", "true");
        storeProps.setProperty("mail.smtp.ssl.enable", "true");
        storeProps.setProperty("mail.imap.socketFactory.port", port);
        storeProps.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        storeProps.setProperty("mail.imap.socketFactory.fallback", "false");
        return storeProps;
    }

}
