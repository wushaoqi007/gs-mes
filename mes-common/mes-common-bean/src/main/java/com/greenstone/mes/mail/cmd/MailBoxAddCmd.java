package com.greenstone.mes.mail.cmd;

import com.alibaba.fastjson2.annotation.JSONField;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBoxAddCmd {

    /**
     * 邮箱本地地址，@前面的部分
     */
    @NotBlank(message = "请填写邮箱地址")
    private String localPart;

    @NotBlank(message = "请填写名称")
    private String name;

    @NotBlank(message = "请填写密码")
    private String password;

    @NotBlank(message = "请确认密码")
    private String password2;

    @NotNull(message = "请填写邮箱容量，单位：MB")
    @Range(min = 100, max = 102400, message = "邮箱容量范围：100-102400 MB")
    private Long quota;

    /**
     * 邮箱类型
     */
    @NotBlank(message = "请指定邮箱类型")
    private String mailboxType;

    /**
     * 个人邮箱创建时需要用户信息
     */
    @NotNull(message = "请指定邮箱的使用人")
    private Long userId;
}
