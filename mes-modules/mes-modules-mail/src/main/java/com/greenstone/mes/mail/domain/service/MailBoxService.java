package com.greenstone.mes.mail.domain.service;

import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.mail.cmd.MailBoxAddCmd;
import com.greenstone.mes.mail.cmd.MailBoxDeleteCmd;
import com.greenstone.mes.mail.cmd.MailBoxEditCmd;
import com.greenstone.mes.mail.infrastructure.persistence.MailBox;
import com.greenstone.mes.mail.interfaces.rest.query.MailboxQuery;

import java.util.List;

public interface MailBoxService {

    MailboxChangeResult createMailBox(MailBoxAddCmd addCmd);

    MailboxChangeResult createMailBoxForNewUser(MailBoxAddCmd addCmd);

    MailboxChangeResult deleteMailBox(MailBoxDeleteCmd deleteCmd);

    void delayedDeleteMailBox(String mailAddress, int delayDays);

    void deleteExpirationMailBox();

    MailBox getMailBox(String email);

    MailboxChangeResult editMailBox(MailBoxEditCmd editCmd);

    void syncMailBoxes();

    List<MailBox> getMailboxes(MailboxQuery mailboxQuery);
}
