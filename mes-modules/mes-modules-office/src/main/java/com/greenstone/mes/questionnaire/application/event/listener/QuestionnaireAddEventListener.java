package com.greenstone.mes.questionnaire.application.event.listener;

import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.event.AssetRevertEvent;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import com.greenstone.mes.asset.application.service.AssetService;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireAddE;
import com.greenstone.mes.questionnaire.application.event.QuestionnaireAddEvent;
import com.greenstone.mes.questionnaire.application.service.QuestionnaireService;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QuestionnaireAddEventListener implements ApplicationListener<QuestionnaireAddEvent> {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireAddEventListener(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @Override
    public void onApplicationEvent(QuestionnaireAddEvent event) {
        QuestionnaireAddE eventData = event.getSource();
        questionnaireService.addEvent(eventData);
    }

}