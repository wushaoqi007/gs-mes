package com.greenstone.mes.questionnaire.application.service;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.questionnaire.application.dto.cqe.FuzzyQuery;
import com.greenstone.mes.questionnaire.application.dto.cqe.QuestionnaireReplyAddCmd;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireAddE;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireReplyE;
import com.greenstone.mes.questionnaire.application.dto.result.QuestionnaireResult;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;

import java.util.List;

public interface QuestionnaireService {
    List<Questionnaire> list(FuzzyQuery fuzzyQuery);

    Questionnaire detail(String id);

    void delete(List<String> ids);

    void suggestions(JSONObject jsonObject);

    // Reply

    void reply(QuestionnaireReplyAddCmd addCmd);

    void deleteReply(String id);

    void addEvent(QuestionnaireAddE eventData);

    void replyEvent(QuestionnaireReplyE eventData);

    List<QuestionnaireResult> exportSuggestions(FuzzyQuery query);
}
