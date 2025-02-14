package com.greenstone.mes.questionnaire.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireDO;
import org.springframework.stereotype.Repository;

/**
 * @author wushaoqi
 * @date 2024-05-29-9:39
 */
@Repository
public interface QuestionnaireMapper extends EasyBaseMapper<QuestionnaireDO> {
}
