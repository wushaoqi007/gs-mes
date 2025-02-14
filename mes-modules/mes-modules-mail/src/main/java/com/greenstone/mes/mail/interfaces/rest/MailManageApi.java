package com.greenstone.mes.mail.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.mail.domain.helper.MailHelper;
import com.greenstone.mes.mail.domain.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/mail/manage")
public class MailManageApi {

    private final MailHelper mailHelper;
    private final MailService mailService;

    @PostMapping("/-/folders")
    public AjaxResult send() {
        mailService.getFolders();
        return AjaxResult.success();
    }


}
