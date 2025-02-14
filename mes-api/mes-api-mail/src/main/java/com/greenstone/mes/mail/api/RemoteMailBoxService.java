package com.greenstone.mes.mail.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.external.dto.result.MailboxChangeResult;
import com.greenstone.mes.external.dto.result.MailboxResult;
import com.greenstone.mes.mail.cmd.MailBoxAddCmd;
import com.greenstone.mes.mail.cmd.MailBoxEditCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Repository
@FeignClient(contextId = "remoteMailBoxService", value = ServiceNameConstants.MAIL_SERVICE)
public interface RemoteMailBoxService {

    @PostMapping("/mail/box")
    MailboxChangeResult addMailBox(@RequestBody MailBoxAddCmd addCmd);

    @DeleteMapping("/mail/box/{username}")
    MailboxChangeResult deleteMailBox(@PathVariable("username") String username);

    @GetMapping("/mail/box/{username}")
    MailboxResult getMailBox(@PathVariable("username") String username);

    @PutMapping("/mail/box")
    MailboxChangeResult editMailBox(@RequestBody MailBoxEditCmd editCmd);
}
