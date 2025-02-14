package com.greenstone.mes.questionnaire.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.questionnaire.application.assembler.QuestionnaireAssembler;
import com.greenstone.mes.questionnaire.application.dto.cqe.FuzzyQuery;
import com.greenstone.mes.questionnaire.application.dto.cqe.QuestionnaireReplyAddCmd;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireAddE;
import com.greenstone.mes.questionnaire.application.dto.event.QuestionnaireReplyE;
import com.greenstone.mes.questionnaire.application.dto.result.QuestionnaireResult;
import com.greenstone.mes.questionnaire.application.event.QuestionnaireAddEvent;
import com.greenstone.mes.questionnaire.application.event.QuestionnaireReplyEvent;
import com.greenstone.mes.questionnaire.application.service.QuestionnaireService;
import com.greenstone.mes.questionnaire.domain.entity.Questionnaire;
import com.greenstone.mes.questionnaire.domain.entity.QuestionnaireReply;
import com.greenstone.mes.questionnaire.domain.repository.QuestionnaireReplyRepository;
import com.greenstone.mes.questionnaire.domain.repository.QuestionnaireRepository;
import com.greenstone.mes.questionnaire.infrastructure.persistence.QuestionnaireConfigDO;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireReplyRepository replyRepository;
    private final RemoteUserService userService;
    private final RemoteMailService mailService;
    private final ApplicationEventPublisher eventPublisher;
    private final QuestionnaireAssembler questionnaireAssembler;

    @Override
    public void suggestions(JSONObject jsonObject) {
        String formKey = jsonObject.getString("formKey");
        if (formKey == null) {
            log.error("问卷格式有误，未找到formKey:{}", jsonObject.toJSONString());
            throw new ServiceException(StrUtil.format("问卷格式有误，未找到formKey:{}", jsonObject.toJSONString()));
        }
        List<QuestionnaireConfigDO> configList = questionnaireRepository.getConfigByFormKey(formKey);
        if (CollUtil.isEmpty(configList)) {
            log.error("问卷{}未配置问卷映射字段，请先去questionnaire_config表配置", formKey);
            throw new ServiceException(StrUtil.format("问卷{}未配置问卷映射字段，请先去questionnaire_config表配置", formKey));
        }
        Questionnaire questionnaire = Questionnaire.builder().id(jsonObject.getString("id")).dataJson(jsonObject.toJSONString()).build();
        for (QuestionnaireConfigDO config : configList) {
            String value = jsonObject.getString(config.getAlias());
            switch (config.getFieldName()) {
                case "user_name" -> questionnaire.setUserName(value);
                case "dept" -> questionnaire.setDept(value);
                case "phone" -> {
                    questionnaire.setPhone(value);
                    setUserInfoByPhone(questionnaire, value);
                }
                case "employee_no" -> {
                    questionnaire.setEmployeeNo(value);
                    setUserInfoByEmployeeNo(questionnaire, value);
                }
            }
        }
        questionnaireRepository.addOrUpdate(questionnaire);
        // 发送邮件
        QuestionnaireAddE questionnaireAddE = questionnaireAssembler.toQuestionnaireAddE(questionnaire);
        eventPublisher.publishEvent(new QuestionnaireAddEvent(questionnaireAddE));
    }

    @Override
    public void reply(QuestionnaireReplyAddCmd addCmd) {
        log.info("新增问卷回复：{}", addCmd);
        Questionnaire questionnaire = questionnaireRepository.detail(addCmd.getQuestionnaireId());
        QuestionnaireReply reply = QuestionnaireReply.builder().questionnaireId(addCmd.getQuestionnaireId()).content(addCmd.getContent()).build();
        reply.setReplyById(SecurityUtils.getLoginUser().getUser().getUserId());
        reply.setReplyBy(SecurityUtils.getLoginUser().getUser().getNickName());
        reply.setReplyTime(LocalDateTime.now());
        replyRepository.add(reply);
        // 发送邮件
        QuestionnaireReplyE questionnaireReplyE = questionnaireAssembler.toQuestionnaireReplyE(questionnaire);
        questionnaireReplyE.setContent(reply.getContent());
        eventPublisher.publishEvent(new QuestionnaireReplyEvent(questionnaireReplyE));
    }

    @Override
    public void deleteReply(String id) {
        log.info("删除问卷回复：{}", id);
        replyRepository.delete(id);
    }

    @Async
    @Override
    public void addEvent(QuestionnaireAddE eventData) {
        log.info("开始发送问卷反馈邮件");
        if (eventData.getEmail() != null) {
            String title = StrUtil.format("【问卷调查-合理化建议】回复");
            String content = StrUtil.format("您的建议我们已经收到，我们会评估审核！");
            MailSendCmd mailSendCmd =
                    MailSendCmd.builder().businessKey("office_questionnaire").serialNo(eventData.getId()).subject(title).content(content).to(List.of(new MailAddress(eventData.getEmail(),
                    null))).html(true).build();
            mailService.send(mailSendCmd);
        }
        log.info("结束发送问卷反馈邮件");
    }

    @Async
    @Override
    public void replyEvent(QuestionnaireReplyE eventData) {
        log.info("开始发送问卷回复邮件");
        if (eventData.getEmail() != null) {
            String title = StrUtil.format("【问卷调查-合理化建议】回复");
            MailSendCmd mailSendCmd = MailSendCmd.builder().businessKey("office_questionnaire").serialNo(eventData.getId()).subject(title).content(eventData.getContent()).to(List.of(new MailAddress(eventData.getEmail(), null))).html(true).build();
            mailService.send(mailSendCmd);
        }
        log.info("结束发送问卷回复邮件");
    }

    @Override
    public List<QuestionnaireResult> exportSuggestions(FuzzyQuery query) {
        log.info("questionnaire list export:{}", query);
        List<Questionnaire> list = questionnaireRepository.list(query);
        List<QuestionnaireResult> results = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                QuestionnaireResult result = QuestionnaireResult.builder().index(i + 1).userName(list.get(i).getUserName()).build();
                JSONObject jsonObject = JSONObject.parseObject(list.get(i).getDataJson());
                String situation = jsonObject.getString("textarea1716877866408");
                String suggest = jsonObject.getString("textarea1716878040811");
                result.setSituation(situation);
                result.setSuggest(suggest);
                List<QuestionnaireReply> questionnaireReplies = replyRepository.listReplyByQuestionnaireId(list.get(i).getId());
                if (CollUtil.isNotEmpty(questionnaireReplies)) {
                    result.setReply(questionnaireReplies.get(0).getContent());
                }
                results.add(result);
            }
        }
        return results;
    }


    public void setUserInfoByEmployeeNo(Questionnaire questionnaire, String employeeNo) {
        if (questionnaire.getUserId() == null) {
            SysUser user = userService.getUser(SysUser.builder().employeeNo(employeeNo).build());
            if (user != null) {
                questionnaire.setUserId(user.getUserId());
                questionnaire.setWxUserId(user.getWxUserId());
                questionnaire.setEmail(user.getEmail());
            }
        }
    }

    public void setUserInfoByPhone(Questionnaire questionnaire, String phone) {
        if (questionnaire.getUserId() == null) {
            SysUser user = userService.getUser(SysUser.builder().phonenumber(phone).build());
            if (user != null) {
                questionnaire.setUserId(user.getUserId());
                questionnaire.setWxUserId(user.getWxUserId());
                questionnaire.setEmail(user.getEmail());
            }
        }
    }

    @Override
    public List<Questionnaire> list(FuzzyQuery fuzzyQuery) {
        log.info("questionnaire list query:{}", fuzzyQuery);
        return questionnaireRepository.list(fuzzyQuery);
    }

    @Override
    public Questionnaire detail(String id) {
        log.info("questionnaire detail query:{}", id);
        return questionnaireRepository.detail(id);
    }


    @Override
    public void delete(List<String> ids) {
        log.info("questionnaire delete:{}", ids);
        questionnaireRepository.delete(ids);
    }

}
