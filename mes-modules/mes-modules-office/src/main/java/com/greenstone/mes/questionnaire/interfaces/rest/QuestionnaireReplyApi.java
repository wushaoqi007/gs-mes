package com.greenstone.mes.questionnaire.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.questionnaire.application.dto.cqe.QuestionnaireReplyAddCmd;
import com.greenstone.mes.questionnaire.application.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/questionnaire/reply")
public class QuestionnaireReplyApi extends BaseController {

    private final QuestionnaireService questionnaireService;

    @PostMapping
    public AjaxResult add(@Validated @RequestBody QuestionnaireReplyAddCmd addCmd) {
        questionnaireService.reply(addCmd);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        questionnaireService.deleteReply(id);
        return AjaxResult.success();
    }

}
