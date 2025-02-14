package com.greenstone.mes.mail.domain.entity;

import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailAttachment;
import com.greenstone.mes.mail.cmd.MailInLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSimple {

    private Long id;

    /**
     * 业务关键字
     */
    private String businessKey;

    /**
     * 业务单据编号
     */
    private String serialNo;

    /**
     * 发送人邮箱，可以为空
     */
    @Nullable
    private String sender;

    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 邮件内容是否为 html
     */
    private boolean html;

    /**
     * 邮件接收人
     */
    private List<MailAddress> to;

    /**
     * 邮件抄送人
     */
    private List<MailAddress> cc;

    /**
     * 邮件内联文件
     */
    private List<MailInLine> inLines;

    /**
     * 邮件附件
     */
    private List<MailAttachment> attachments;

    /**
     * 重试次数
     */
    @Builder.Default
    private Integer retryTimes = 0;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private Integer maxRetryTimes = 0;

    /**
     * 错误信息
     */
    private String errorMsg;

    public boolean isEnd() {
        return retryTimes >= maxRetryTimes;
    }

    public boolean isNewMail() {
        return retryTimes == 0;
    }

    public void appendErrorMsg(String errorMsg) {
        if (this.errorMsg == null) {
            this.errorMsg = errorMsg;
        } else {
            this.errorMsg = this.errorMsg + ", " + errorMsg;
        }
    }
}
