package com.greenstone.mes.mail.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailAttachment;
import com.greenstone.mes.mail.cmd.MailInLine;
import com.greenstone.mes.mail.domain.entity.MailFinal;
import com.greenstone.mes.mail.domain.entity.MailSimple;
import com.greenstone.mes.mail.domain.entity.MailStore;
import com.greenstone.mes.mail.domain.repository.MailRequestRepository;
import com.greenstone.mes.mail.domain.service.MailSendService;
import com.greenstone.mes.mail.domain.service.MailService;
import com.greenstone.mes.mail.domain.service.MailStoreService;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import com.greenstone.mes.mail.infrastructure.mapper.MailRequestMapper;
import com.greenstone.mes.mail.infrastructure.mapper.MailUserSettingMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailRequest;
import com.greenstone.mes.mail.infrastructure.persistence.MailUserSetting;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    private final MailSendService mailSendService;
    private final MailStoreService mailStoreService;
    private final RemoteFileService fileService;
    private final MailRequestRepository mailRequestRepository;
    private final MsgProducer<MailSendResult> msgProducer;
    private final MailRequestMapper mailRequestMapper;
    private final MailConfig mailConfig;
    private final MailUserSettingMapper mailUserSettingMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);


    @Override
    public MailSendResult send(MailSimple mailSimple) {
        return send(mailSimple, false);
    }

    @Override
    public void sendAsync(MailSimple mailSimple) {
        executorService.submit(() -> {
            send(mailSimple, true);
        });
    }

    @Override
    public MailSendResult resend(Long mailId) {
        MailRequest mailRequest = mailRequestMapper.selectById(mailId);
        if (mailRequest == null) {
            throw new RuntimeException("不存在id为" + mailId + "的邮件");
        }
        MailSimple mailSimple = JSON.parseObject(mailRequest.getMailJson(), MailSimple.class);
        return send(mailSimple);
    }

    @Override
    public void resendAsync(Long mailId) {
        MailRequest mailRequest = mailRequestMapper.selectById(mailId);
        if (mailRequest == null) {
            throw new RuntimeException("不存在id为" + mailId + "的邮件");
        }
        MailSimple mailSimple = JSON.parseObject(mailRequest.getMailJson(), MailSimple.class);
        sendAsync(mailSimple);
    }

    @Override
    public void getFolders() {
        String email = SecurityUtils.getLoginUser().getUser().getEmail();
        MailUserSetting mailUserSetting = mailUserSettingMapper.selectById(email);
        if (mailUserSetting == null || mailUserSetting.getAppPassword() == null) {
            throw new ServiceException("请先设置应用密码");
        }
        MailStore mailStore = MailStore.newInstance(mailConfig.getServer().getHost(), "993", email, mailUserSetting.getAppPassword());
        mailStore.getFolders();
    }

    @Override
    public void searchMails() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK - (Calendar.DAY_OF_WEEK - 1)) - 1);
        Date mondayDate = calendar.getTime();
        SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, mondayDate);
        SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, new Date());
        SearchTerm comparisonAndTerm = new AndTerm(comparisonTermGe, comparisonTermLe);
//        Message[] messages = folder.search(comparisonAndTerm);
    }

    private MailSendResult send(MailSimple mailSimple, boolean async) {
        if (mailSimple.getErrorMsg() != null) {
            log.warn("准备发送邮件时出现问题: {}", mailSimple.getErrorMsg());
        }
        MailFinal mail = null;
        MimeMessage mimeMessage = null;

        String errMsg = null;
        boolean buildSuccess = false;
        boolean sendSuccess = false;

        // 构建邮件信息
        try {
            mail = buildMail(mailSimple);
            // 构建最终发送的消息
            mimeMessage = mailSendService.buildMimeMessage(mail);
            buildSuccess = true;
        } catch (MailException ex) {
            log.error("构建邮件失败", ex);
            errMsg = "邮件内信息错误，请重新填写并尝试再次发送。";
        }

        // 发送邮件
        log.info("开始发送邮件");
        LocalDateTime startTime = LocalDateTime.now();
        if (buildSuccess) {
            try {
                mailSendService.sendMessage(mimeMessage, mail.getSender());
                sendSuccess = true;
            } catch (MailException ex) {
                log.error("发送邮件失败", ex);
                errMsg = "发送邮件失败: " + ex.getMessage();
            }
        }
        LocalDateTime endTIme = LocalDateTime.now();
        MailRequest mailRequest = mailRequestRepository.saveSendRecord(mailSimple, startTime, endTIme, sendSuccess, getMailSize(mail, mimeMessage));

        // 将邮件保存到已发送
        if (sendSuccess) {
            try {
                mailStoreService.storeSentMessage(mimeMessage, mail.getSender());
            } catch (MailException ex) {
                log.error("保存邮件失败", ex);
                errMsg = "保存邮件失败: " + ex.getMessage();
            }
        }

        log.info("邮件发送结束");

        MailSendResult mailSendResult = buildMailSendResult(mailRequest, errMsg);

        if (async) {
            try {
                msgProducer.send(MqConst.Topic.MAIL_SEND_RESULT, mailSendResult);
            } catch (ExecutionException | InterruptedException e) {
                log.error("发送mq消息失败， topic: {}, content: {}", MqConst.Topic.MAIL_SEND_RESULT, JSON.toJSONString(mailSendResult));
            }
        }

        return mailSendResult;
    }


    private MailFinal buildMail(MailSimple mailSimple) {
        MailFinal mail = new MailFinal();
        mail.setSender(mailSimple.getSender());
        mail.setSubject(mailSimple.getSubject());
        mail.setContent(mailSimple.getContent());
        mail.setHtml(mailSimple.isHtml());
        mail.setTo(toAddress(mailSimple.getTo()));
        mail.setCc(toAddress(mailSimple.getCc()));
        mail.setInlines(toInLines(mailSimple.getInLines()));
        mail.setAttachments(toAttachments(mailSimple.getAttachments()));
        return mail;
    }

    private List<MailFinal.Attachment> toAttachments(List<MailAttachment> mailAttachments) {
        if (CollUtil.isEmpty(mailAttachments)) {
            return null;
        }
        List<MailFinal.Attachment> attachments = new ArrayList<>();

        for (MailAttachment mailAttachment : mailAttachments) {
            try {
                attachments.add(
                        MailFinal.Attachment.builder().name(mailAttachment.getName())
                                .content(getResource(mailAttachment.getPath())).build()
                );
            } catch (IOException e) {
                throw new RuntimeException("mail send error: get attachment '" + mailAttachment.getPath() + "' failed", e);
            }
        }

        return attachments;
    }

    private List<InternetAddress> toAddress(List<MailAddress> mailAddresses) {
        if (CollUtil.isEmpty(mailAddresses)) {
            return null;
        }
        List<InternetAddress> addresses = new ArrayList<>();
        try {
            for (MailAddress mailAddress : mailAddresses) {
                addresses.add(new InternetAddress(mailAddress.getAddress(), mailAddress.getPersonal()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("接收人或抄送人设置错误");
        }
        return addresses;
    }

    private List<File> toInLines(List<MailInLine> inLines) {
        if (CollUtil.isEmpty(inLines)) {
            return null;
        }
        List<File> files = new ArrayList<>();
        for (MailInLine inLine : inLines) {
            File file;
            try {
                file = getFiles(inLine.getPath());
            } catch (IOException e) {
                throw new RuntimeException("Mail send error: get inLine '" + inLine.getPath() + "' failed", e);
            }
            files.add(file);
        }
        return files;
    }

    private ByteArrayResource getResource(String path) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("path", path);
        Response response = fileService.download(data);
        byte[] bytes = IOUtils.toByteArray(response.body().asInputStream());
        return new ByteArrayResource(bytes);
    }

    private File getFiles(String path) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("path", path);
        Response response = fileService.download(data);
        File file = File.createTempFile(UUID.fastUUID().toString(), ".tmp");
        FileUtils.copyInputStreamToFile(response.body().asInputStream(), file);
        return file;
    }

    private int getMailSize(MailFinal mailFinal, MimeMessage mimeMessage) {
        int contentSize = 0;
        try {
            contentSize = mimeMessage == null ? 0 : mimeMessage.getSize();
        } catch (MessagingException e) {
            log.warn("无法获取邮件内容大小", e);
        }
        return (mailFinal.getAttachmentsSize() + contentSize) / 8 / 1024;
    }

    private MailSendResult buildMailSendResult(MailRequest mailRequest, String errMsg) {
        return MailSendResult.builder().mailId(mailRequest.getId())
                .businessKey(mailRequest.getBusinessKey())
                .serialNo(mailRequest.getSerialNo())
                .status(mailRequest.getStatus())
                .retryTimes(mailRequest.getRetryTimes())
                .maxRetryTimes(mailRequest.getMaxRetryTimes())
                .errorMsg(errMsg).build();
    }

}
