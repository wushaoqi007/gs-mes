package com.greenstone.mes.questionnaire.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.questionnaire.domain.converter.QuestionnaireConverter;
import com.greenstone.mes.questionnaire.domain.entity.QuestionnaireReply;
import com.greenstone.mes.questionnaire.infrastructure.mapper.QuestionnaireReplyMapper;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireReplyDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class QuestionnaireReplyRepository {
    private final QuestionnaireReplyMapper replyMapper;
    private final QuestionnaireConverter questionnaireConverter;

    public List<QuestionnaireReply> listReplyByQuestionnaireId(String questionnaireId) {
        QueryWrapper<QuestionnaireReplyDO> queryWrapper = Wrappers.query(QuestionnaireReplyDO.builder().questionnaireId(questionnaireId).build());
        queryWrapper.orderByDesc("reply_time");
        List<QuestionnaireReplyDO> list = replyMapper.selectList(queryWrapper);
        return questionnaireConverter.replyDos2Entities(list);
    }

    public void add(QuestionnaireReply reply) {
        QuestionnaireReplyDO saveDO = questionnaireConverter.replyEntity2Do(reply);
        replyMapper.insert(saveDO);
    }

    public void delete(String id) {
        replyMapper.deleteById(id);
    }
}
