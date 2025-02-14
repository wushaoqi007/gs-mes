package com.greenstone.mes.questionnaire.domain.converter;

import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import com.greenstone.mes.questionnaire.domain.entity.QuestionnaireReply;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireDO;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireReplyDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuestionnaireConverter {

    QuestionnaireDO entity2Do(Questionnaire questionnaire);

    List<QuestionnaireDO> entities2Dos(List<Questionnaire> questionnaires);

    Questionnaire do2Entity(QuestionnaireDO questionnaireDO);

    List<Questionnaire> dos2Entities(List<QuestionnaireDO> questionnaireDOS);

    // Reply
    QuestionnaireReplyDO replyEntity2Do(QuestionnaireReply questionnaireReply);

    List<QuestionnaireReplyDO> replyEntities2Dos(List<QuestionnaireReply> questionnaireReplies);


    QuestionnaireReply replyDo2Entity(QuestionnaireReplyDO questionnaireReplyDO);

    List<QuestionnaireReply> replyDos2Entities(List<QuestionnaireReplyDO> questionnaireReplyDOS);


}
