package com.greenstone.mes.mail.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.mail.cmd.MailBoxAddCmd;
import com.greenstone.mes.mail.cmd.MailBoxDeleteCmd;
import com.greenstone.mes.mail.cmd.MailBoxEditCmd;
import com.greenstone.mes.mail.domain.service.MailBoxService;
import com.greenstone.mes.mail.infrastructure.persistence.MailBox;
import com.greenstone.mes.mail.interfaces.rest.query.MailboxQuery;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/mail/box")
public class MailboxApi extends BaseController {

    private final MailBoxService mailBoxService;

    @GetMapping
    public TableDataInfo getMailBoxes(MailboxQuery mailboxQuery) {
        startPage();
        List<MailBox> mailboxes = mailBoxService.getMailboxes(mailboxQuery);
        return getDataTable(mailboxes);
    }

    @GetMapping("/{email}")
    public AjaxResult getMailBox(@PathVariable("email") String email) {
        return AjaxResult.success(mailBoxService.getMailBox(email));
    }

    @Transactional
    @PostMapping
    public AjaxResult addMailBox(@RequestBody @Validated MailBoxAddCmd addCmd) {
        return AjaxResult.success(mailBoxService.createMailBox(addCmd));
    }

    @Transactional
    @DeleteMapping("/{email}")
    public AjaxResult deleteMailBox(@PathVariable("email") String email) {
        return AjaxResult.success(mailBoxService.deleteMailBox(MailBoxDeleteCmd.builder().email(email).reason("管理员手动删除").build()));
    }

    @Transactional
    @PutMapping
    public AjaxResult editMailBox(@RequestBody @Validated MailBoxEditCmd editCmd) {
        return AjaxResult.success(mailBoxService.editMailBox(editCmd));
    }

    @PostMapping("/sync2mes")
    public AjaxResult syncAllMailBoxes() {
        mailBoxService.syncMailBoxes();
        return AjaxResult.success();
    }

}
