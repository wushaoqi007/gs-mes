package com.greenstone.mes.external.domain.entity;

import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Mail {

    private String sender;

    private String subject;

    private String content;

    private boolean html;

    private List<Attachment> attachments;

    private List<File> inlines;

    private List<InternetAddress> to = new ArrayList<>();

    private List<InternetAddress> cc = new ArrayList<>();

    public void addTo(String address) {
        try {
            this.to.add(new InternetAddress(address));
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTo(String address, String personal) {
        try {
            this.to.add(new InternetAddress(address, personal, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("构建邮件接受者失败");
        }
    }

    public void addCc(String address) {
        try {
            this.cc.add(new InternetAddress(address));
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCc(String address, String personal) {
        try {
            this.cc.add(new InternetAddress(address, personal, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("构建邮件接受者失败");
        }
    }

    public void addAttachment(Attachment file) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(file);
    }

    public void addAttachments(List<Attachment> files) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.addAll(files);
    }

    public void addInline(File file) {
        if (this.inlines == null) {
            this.inlines = new ArrayList<>();
        }
        this.inlines.add(file);
    }

    public void addInlines(List<File> files) {
        if (this.inlines == null) {
            this.inlines = new ArrayList<>();
        }
        this.inlines.addAll(files);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attachment {
        private String name;

        private ByteArrayResource content;
    }
}
