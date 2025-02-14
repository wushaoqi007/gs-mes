package com.greenstone.mes.questionnaire.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireAddE;

public class QuestionnaireAddEvent extends BaseApplicationEvent<QuestionnaireAddE> {

    public QuestionnaireAddEvent(QuestionnaireAddE source) {
        super(source);
    }

}
