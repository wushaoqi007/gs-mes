package com.greenstone.mes.external.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailboxChangeResult {

    private boolean success;

    private String email;

    private String message;

    private String type;

}
