package com.greenstone.mes.questionnaire.application.assembler;

import com.greenstone.mes.asset.domain.converter.EnumConverter;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireAddE;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireReplyE;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EnumConverter.class}
)
public interface QuestionnaireAssembler {

    QuestionnaireAddE toQuestionnaireAddE(Questionnaire questionnaire);

    QuestionnaireReplyE toQuestionnaireReplyE(Questionnaire questionnaire);
}
