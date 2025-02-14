package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBoxDeleteCmd {

    @NotBlank(message = "邮箱地址不能为空")
    private String email;

    private String reason;
}
