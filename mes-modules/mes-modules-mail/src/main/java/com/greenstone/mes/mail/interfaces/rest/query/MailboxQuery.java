package com.greenstone.mes.mail.interfaces.rest.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailboxQuery {

    private String email;

    private String mailboxType;

    private Long userId;

}
