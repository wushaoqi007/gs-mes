package com.greenstone.mes.mail.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.*;

@ConfigurationProperties(prefix = "mail")
@Component
@Data
public class MailConfig {

    private String domain;

    private String defaultSender;

    private Server server;

    private List<Mailbox> mailboxes;

    @Data
    public static class Mailbox {
        private String host;
        private String username;
        private String password;
        private String personal;
        private String charset;
        private InternetAddress address;
        private JavaMailSender mailSender;
    }

    @Data
    public static class Server {
        private String apikey;
        private String host;
        private String baseUrl;
    }

    public Mailbox getMailbox(String username) {
        return mailboxes.stream().filter(mailbox -> username.equals(mailbox.getUsername())).findFirst().orElse(null);
    }

    private Optional<Mailbox> getSender(String username) {
        return mailboxes.stream().filter(mailbox -> mailbox.getUsername().equals(username)).findFirst();
    }

    public static final Map<String, Mailbox> senderMap = new HashMap<>();

    @Nullable
    public Mailbox getMailSender(String username) {
        return senderMap.computeIfAbsent(username, s -> getSender(s).map(mailbox -> {
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(mailbox.getHost());
            javaMailSender.setUsername(mailbox.getUsername());
            javaMailSender.setPassword(mailbox.getPassword());
            javaMailSender.setDefaultEncoding("UTF-8");
            Properties javaMailProperties = new Properties();
            javaMailProperties.setProperty("mail.smtp.auth", "true");
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
            javaMailProperties.setProperty("mail.smtp.ssl.enable", "true");
            javaMailProperties.setProperty("mail.smtp.port", "465");
            javaMailProperties.setProperty("mail.smtp.socketFactory.port", "465");
            javaMailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
            javaMailSender.setJavaMailProperties(javaMailProperties);

            mailbox.setMailSender(javaMailSender);
            try {
                mailbox.setAddress(new InternetAddress(mailbox.getUsername(), mailbox.getPersonal(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return mailbox;
        }).orElse(null));
    }

}
