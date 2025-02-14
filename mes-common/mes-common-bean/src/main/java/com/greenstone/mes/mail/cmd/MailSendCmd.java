package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSendCmd {

    /**
     * 邮件id，重新发送时需要用到
     */
    private Long mailId;

    /**
     * 业务关键字
     */
    @NotBlank(message = "缺少业务关键字或功能id")
    private String businessKey;

    /**
     * 业务单据编号（如果通知时类型的没有编号就设置一个uuid）
     */
    @NotBlank(message = "缺少单据编号或id")
    private String serialNo;

    /**
     * 发送人邮箱，可以为空，为空时使用默认的发送人
     */
    @Nullable
    private String sender;

    /**
     * 邮件标题
     */
    @NotBlank(message = "邮件标题不能为空")
    private String subject;

    /**
     * 邮件内容
     */
    @NotBlank(message = "邮件内容不能为空")
    private String content;

    /**
     * 邮件内容是否为 html
     */
    private boolean html;

    /**
     * 邮件接收人
     */
    @Valid
    private List<MailAddress> to;

    private List<Long> toUserIds;

    /**
     * 邮件抄送人
     */
    @Valid
    private List<MailAddress> cc;

    private List<Long> ccUserIds;

    /**
     * 邮件内联文件
     */
    @Valid
    private List<MailInLine> inLines;

    /**
     * 邮件附件
     */
    @Valid
    private List<MailAttachment> attachments;

}
