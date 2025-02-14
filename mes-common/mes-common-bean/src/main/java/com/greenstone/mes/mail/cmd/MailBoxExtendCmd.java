package com.greenstone.mes.mail.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailBoxExtendCmd {

    private String email;

    /**
     * 单位：MB
     */
    private String quota;
}
