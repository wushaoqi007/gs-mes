package com.greenstone.mes.questionnaire.application.event;

import com.greenstone.mes.common.event.BaseApplicationEvent;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireReplyE;

public class QuestionnaireReplyEvent extends BaseApplicationEvent<QuestionnaireReplyE> {

    public QuestionnaireReplyEvent(QuestionnaireReplyE source) {
        super(source);
    }

}
