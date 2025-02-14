package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBoxEditCmd {

    @NotBlank(message = "邮箱地址不能为空")
    private String email;

    private String name;

    private String password;

    private String password2;

    @Min(value = 1024, message = "容量最小为1024MB")
    @Max(value = 102400, message = "容量最大为102400MB")
    private Long quota;

    private List<String> tags;

    private Long userId;

    private String mailboxType;

}
