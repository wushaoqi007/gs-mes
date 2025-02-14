package com.greenstone.mes.office.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailBoxExtendCmd {

    private String mailAddress;

    /**
     * 单位：MB
     */
    private String quota;

    private String wxCpId;

    private String wxUserId;

}
