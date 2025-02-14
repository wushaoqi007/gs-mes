package com.greenstone.mes.mail.external;

import com.greenstone.mes.external.dto.result.DomainResult;
import com.greenstone.mes.mail.external.dto.MailboxOriginResult;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.mail.external.dto.MailboxCreate;
import com.greenstone.mes.mail.external.dto.MailboxUpdate;

import java.util.List;

public interface MailBoxExternalService {

    MailboxChangeResult createMailBox(MailboxCreate create);

    MailboxChangeResult deleteMailBox(String email);

    MailboxOriginResult getMailBox(String username);

    MailboxChangeResult editMailBox(MailboxUpdate update);

    List<MailboxOriginResult> getMailBoxesByDomain(String domain);

    DomainResult getDomain(String domain);

}