package com.greenstone.mes.mail.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.mail.domain.entity.MailFinal;
import com.greenstone.mes.mail.domain.service.MailSendService;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSendServiceImpl implements MailSendService {

    private final MailConfig mailConfig;

    @Override
    public void sendMessage(MimeMessage mimeMessage, String username) {
        JavaMailSender mailSender = getMailSender(username);
        mailSender.send(mimeMessage);
    }

    @Override
    public MimeMessage buildMimeMessage(MailFinal mail) {
        if (StrUtil.isBlank(mail.getSender())) {
            mail.setSender(mailConfig.getDefaultSender());
            log.info("Send with default sender, cause given an empty sender.");
        }

        MailConfig.Mailbox mailbox = mailConfig.getMailSender(mail.getSender());
        if (mailbox == null) {
            throw new RuntimeException("Send mail failed, can not find config with mailbox: " + mail.getSender());
        }

        JavaMailSender mailSender = getMailSender(mail.getSender());
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            boolean multipart = ArrayUtil.isNotEmpty(mail.getAttachments());
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart);

            // 发件人
            InternetAddress fromAddress = new InternetAddress(mailbox.getUsername(), mailbox.getPersonal(), "UTF-8");
            helper.setFrom(fromAddress);
            // 收件人
            if (CollUtil.isNotEmpty(mail.getTo())) {
                helper.setTo(ArrayUtil.toArray(mail.getTo(), InternetAddress.class));
            }
            // 抄送人
            if (CollUtil.isNotEmpty(mail.getCc())) {
                helper.setCc(ArrayUtil.toArray(mail.getCc(), InternetAddress.class));
            }
            // 标题
            helper.setSubject(mail.getSubject());
            // 内容
            helper.setText(mail.getContent(), mail.isHtml());
            // 内联文件
            if (CollUtil.isNotEmpty(mail.getInlines())) {
                for (File inline : mail.getInlines()) {
                    helper.addInline(inline.getName(), inline);
                }
            }
            // 附件
            if (CollUtil.isNotEmpty(mail.getAttachments())) {
                for (MailFinal.Attachment attachment : mail.getAttachments()) {
                    helper.addAttachment(MimeUtility.decodeText(attachment.getName()), attachment.getContent());
                }
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("Send mail failed, can not assemble message", e);
        }

        return mimeMessage;
    }

    private JavaMailSender getMailSender(String username) {
        MailConfig.Mailbox mailbox = mailConfig.getMailbox(username);
        return getMailSender(createProps(), mailbox.getHost(), mailbox.getUsername(), mailbox.getPassword());
    }

    private final Map<String, JavaMailSender> senderMap = new HashMap<>();

    private JavaMailSender getMailSender(Properties props, String host, String username, String password) {
        return senderMap.computeIfAbsent(username, new Function<String, JavaMailSender>() {
            @Override
            public JavaMailSender apply(String username) {
                JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
                javaMailSender.setHost(host);
                javaMailSender.setUsername(username);
                javaMailSender.setPassword(password);
                javaMailSender.setDefaultEncoding("UTF-8");
                javaMailSender.setJavaMailProperties(props);
                return javaMailSender;
            }
        });
    }

    private Properties createProps() {
        Properties sendProps = System.getProperties();
        sendProps.setProperty("mail.transport.protocol", "smtp");
        sendProps.setProperty("mail.smtp.host", mailConfig.getServer().getHost());
        sendProps.setProperty("mail.smtp.auth", "true");
        sendProps.setProperty("mail.smtp.port", "465");
        sendProps.setProperty("mail.smtp.starttls.enable", "true");
        sendProps.setProperty("mail.smtp.ssl.enable", "true");
        sendProps.setProperty("mail.smtp.socketFactory.port", "465");
        sendProps.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sendProps.setProperty("mail.smtp.socketFactory.fallback", "false");
        return sendProps;
    }

}