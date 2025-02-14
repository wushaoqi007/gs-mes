package com.greenstone.mes.questionnaire.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.questionnaire.application.dto.cqe.FuzzyQuery;
import com.greenstone.mes.questionnaire.domain.converter.QuestionnaireConverter;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import com.greenstone.mes.questionnaire.domain.entity.QuestionnaireReply;
import com.greenstone.mes.questionnaire.infrastructure.mapper.QuestionnaireConfigMapper;
import com.greenstone.mes.questionnaire.infrastructure.mapper.QuestionnaireMapper;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireConfigDO;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class QuestionnaireRepository {
    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionnaireConfigMapper configMapper;
    private final QuestionnaireConverter questionnaireConverter;
    private final QuestionnaireReplyRepository replyRepository;

    public List<QuestionnaireConfigDO> getConfigByFormKey(String formKey) {
        return configMapper.list(QuestionnaireConfigDO.builder().formKey(formKey).build());
    }

    public List<Questionnaire> list(FuzzyQuery fuzzyQuery) {
        QueryWrapper<QuestionnaireDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        List<QuestionnaireDO> questionnaireDOS = questionnaireMapper.selectList(fuzzyQueryWrapper);
        return questionnaireConverter.dos2Entities(questionnaireDOS);
    }

    public Questionnaire detail(String id) {
        QuestionnaireDO questionnaireDO = questionnaireMapper.selectById(id);
        if (questionnaireDO == null) {
            throw new ServiceException(StrUtil.format("未找到问卷记录，id:{}", id));
        }
        Questionnaire questionnaire = questionnaireConverter.do2Entity(questionnaireDO);
        // 查询回复
        List<QuestionnaireReply> replyList = replyRepository.listReplyByQuestionnaireId(questionnaireDO.getId());
        questionnaire.setReplies(replyList);
        return questionnaire;
    }

    public void addOrUpdate(Questionnaire questionnaire) {
        QuestionnaireDO saveDO = questionnaireConverter.entity2Do(questionnaire);
        QuestionnaireDO getById = questionnaireMapper.selectById(questionnaire.getId());
        if (getById != null) {
            questionnaireMapper.updateById(saveDO);
        } else {
            questionnaireMapper.insert(saveDO);
        }
    }

    public void delete(List<String> ids) {
        LambdaQueryWrapper<QuestionnaireDO> removeWrapper = Wrappers.lambdaQuery(QuestionnaireDO.class).in(QuestionnaireDO::getId, ids);
        questionnaireMapper.delete(removeWrapper);
    }
}
