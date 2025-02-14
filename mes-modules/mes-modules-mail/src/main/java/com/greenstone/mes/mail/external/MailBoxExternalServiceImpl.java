package com.greenstone.mes.mail.external;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.external.dto.result.DomainResult;
import com.greenstone.mes.mail.external.dto.MailboxOriginChangeResult;
import com.greenstone.mes.mail.external.dto.MailboxOriginResult;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.mail.external.dto.MailboxCreate;
import com.greenstone.mes.mail.external.dto.MailboxUpdate;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import com.greenstone.mes.mail.infrastructure.consts.MailApiPathConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailBoxExternalServiceImpl implements MailBoxExternalService {

    private final MailHttpClient httpClient;

    private final MailConfig mailConfig;

    @Override
    public MailboxChangeResult createMailBox(MailboxCreate create) {
        String path = getFullPath(MailApiPathConst.MailBox.CREATE);
        String body = httpClient.post(path, create);
        List<MailboxOriginChangeResult> mailboxOriginChangeResults = JSON.parseArray(body, MailboxOriginChangeResult.class);
        MailboxOriginChangeResult originResult = mailboxOriginChangeResults.get(mailboxOriginChangeResults.size() - 1);
        MailboxChangeResult changeResult = originResult.buildChangeResult();
        changeResult.setEmail(create.getLocalPart() + "@" + create.getDomain());
        return changeResult;
    }

    @Override
    public MailboxChangeResult deleteMailBox(String email) {
        String path = getFullPath(MailApiPathConst.MailBox.DELETE);
        String body = httpClient.post(path, List.of(email));
        List<MailboxOriginChangeResult> mailboxOriginChangeResults = JSON.parseArray(body, MailboxOriginChangeResult.class);
        MailboxOriginChangeResult originResult = mailboxOriginChangeResults.get(mailboxOriginChangeResults.size() - 1);
        MailboxChangeResult changeResult = originResult.buildChangeResult();
        changeResult.setEmail(email);
        return changeResult;
    }

    @Override
    public MailboxOriginResult getMailBox(String username) {
        String path = getFullPath(MailApiPathConst.MailBox.GET).replace("{id}", username);
        String body = httpClient.get(path);
        return JSON.parseObject(body, MailboxOriginResult.class);
    }

    @Override
    public MailboxChangeResult editMailBox(MailboxUpdate update) {
        String path = getFullPath(MailApiPathConst.MailBox.EDIT);
        String body = httpClient.post(path, update);
        List<MailboxOriginChangeResult> mailboxOriginChangeResults = JSON.parseArray(body, MailboxOriginChangeResult.class);
        MailboxOriginChangeResult originResult = mailboxOriginChangeResults.get(mailboxOriginChangeResults.size() - 1);
        MailboxChangeResult changeResult = originResult.buildChangeResult();
        changeResult.setEmail(update.getItems().get(0));
        return changeResult;
    }

    @Override
    public List<MailboxOriginResult> getMailBoxesByDomain(String domain) {
        String path = getFullPath(MailApiPathConst.MailBox.GET_MAILBOXES).replace("{domain}", domain);
        String body = httpClient.get(path);
        return JSON.parseArray(body, MailboxOriginResult.class);
    }

    @Override
    public DomainResult getDomain(String domain) {
        String path = getFullPath(MailApiPathConst.Domain.GET).replace("{id}", domain);
        String body = httpClient.get(path);
        return JSON.parseObject(body, DomainResult.class);
    }

    private String getFullPath(String path) {
        return mailConfig.getServer().getBaseUrl() + path;
    }

}
