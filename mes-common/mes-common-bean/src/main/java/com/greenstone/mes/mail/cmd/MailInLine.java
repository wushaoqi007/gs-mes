package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 邮件内联文件
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailInLine {

    @NotEmpty(message = "内联文件名称不能为空")
    private String name;

    @NotEmpty(message = "内联文件地址不能为空")
    private String path;

}
