package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailAttachment {

    @NotEmpty(message = "附件名称不能为空")
    private String name;

    @NotEmpty(message = "附件路径不能为空")
    private String path;
}