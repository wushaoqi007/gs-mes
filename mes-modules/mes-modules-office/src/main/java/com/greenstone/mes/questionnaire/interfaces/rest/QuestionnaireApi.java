package com.greenstone.mes.questionnaire.interfaces.rest;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.datascope.annotation.DataScope;
import com.greenstone.mes.questionnaire.application.dto.cqe.FuzzyQuery;
import com.greenstone.mes.questionnaire.application.dto.result.QuestionnaireResult;
import com.greenstone.mes.questionnaire.application.service.QuestionnaireService;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireApi extends BaseController {

    private final QuestionnaireService questionnaireService;

    @PostMapping("/suggestions")
    public void suggestions(@RequestBody Map<Object, Object> body) {
        log.info("收到问卷：{}", JSON.toJSONString(body));
        JSONObject jsonObject = JSONObject.from(body);
        questionnaireService.suggestions(jsonObject);
    }

    @DataScope(userField = "user_id", suitRoleKeys = "questionnaire_participants,questionnaire_admin", pageable = true)
    @GetMapping("/suggestions/list")
    public TableDataInfo list(FuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("userName");
        fields.add("employeeNo");
        fields.add("phone");
        query.setFields(fields);
        List<Questionnaire> list = questionnaireService.list(query);
        return getDataTable(list);
    }

    @GetMapping(value = "/suggestions/{id}")
    public AjaxResult detail(@PathVariable("id") String id) {
        startPage();
        Questionnaire questionnaire = questionnaireService.detail(id);
        return AjaxResult.success(questionnaire);
    }

    @PostMapping("/suggestions/export")
    public void export(HttpServletResponse response, @RequestBody FuzzyQuery query) {
        List<String> fields = new ArrayList<>();
        fields.add("userName");
        fields.add("employeeNo");
        fields.add("phone");
        query.setFields(fields);
        List<QuestionnaireResult> list = questionnaireService.exportSuggestions(query);
        ExcelUtil<QuestionnaireResult> util = new ExcelUtil<>(QuestionnaireResult.class);
        util.exportExcel(response, list, "改善建议数据" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss"));
    }

}
