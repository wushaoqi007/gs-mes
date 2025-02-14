package com.greenstone.mes.questionnaire.application.event.listener;

import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireReplyE;
import com.greenstone.mes.questionnaire.application.event.QuestionnaireReplyEvent;
import com.greenstone.mes.questionnaire.application.service.QuestionnaireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QuestionnaireReplyEventListener implements ApplicationListener<QuestionnaireReplyEvent> {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireReplyEventListener(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @Override
    public void onApplicationEvent(QuestionnaireReplyEvent event) {
        QuestionnaireReplyE eventData = event.getSource();
        questionnaireService.replyEvent(eventData);
    }

}