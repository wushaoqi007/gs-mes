package com.greenstone.mes.mail.external.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MailboxCreate {

    /**
     * 邮箱状态，固定为1
     */
    private String active = "1";

    /**
     * 域名，固定的
     */
    private String domain;

    /**
     * 邮箱本地地址，@前面的部分
     */
    @NotBlank(message = "请填写邮箱地址")
    @JSONField(name = "local_part")
    private String localPart;

    @NotBlank(message = "请填写名称")
    private String name;

    @NotBlank(message = "请填写密码")
    private String password;

    @NotBlank(message = "请确认密码")
    private String password2;

    @NotBlank(message = "请填写邮箱容量，单位：MB")
    @Size(min = 100, max = 102400, message = "邮箱容量范围：100-102400 MB之间")
    private Long quota;

    /**
     * 登陆时强制更新密码
     */
    @JSONField(name = "force_pw_update")
    private String forcePwUpdate = "0";

    /**
     * 入站使用tls
     */
    @JSONField(name = "tls_enforce_in")
    private String tlsEnforceIn = "0";

    /**
     * 出站使用tls
     */
    @JSONField(name = "tls_enforce_out")
    private String tlsEnforceOut = "0";

}
