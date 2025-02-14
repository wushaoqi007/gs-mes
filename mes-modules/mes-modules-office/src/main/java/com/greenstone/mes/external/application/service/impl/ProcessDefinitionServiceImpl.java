package com.greenstone.mes.external.application.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.external.application.assembler.ProcessAssembler;
import com.greenstone.mes.external.application.dto.cmd.ProcessSaveCmd;
import com.greenstone.mes.external.application.dto.query.ProcDefQuery;
import com.greenstone.mes.external.application.dto.result.ProcessDefSaveResult;
import com.greenstone.mes.external.application.dto.result.ProcessDefinitionResult;
import com.greenstone.mes.external.application.service.ProcessDefinitionService;
import com.greenstone.mes.external.application.service.ProcessNodeDefService;
import com.greenstone.mes.external.domain.dto.cmd.FlwDeployCmd;
import com.greenstone.mes.external.domain.dto.result.FlwDeployResult;
import com.greenstone.mes.external.domain.entity.ProcessDefinition;
import com.greenstone.mes.external.domain.entity.node.FlowNode;
import com.greenstone.mes.external.domain.helper.FlowNodeHelper;
import com.greenstone.mes.external.domain.repository.ProcessDefinitionRepository;
import com.greenstone.mes.external.domain.service.BPMN20Generator;
import com.greenstone.mes.external.domain.service.FlowableExecutor;
import com.greenstone.mes.form.domain.service.GeneralFormService;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:52
 */
@Slf4j
@AllArgsConstructor
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    private final BPMN20Generator bpmn20Generator;
//    private final FlowableExecutor flowableExecutor;
    private final ProcessDefinitionRepository processDefinitionRepository;
    private final ProcessNodeDefService processNodeDefService;
//    private final ProcessAssembler assembler;
    private final FlowNodeHelper flowNodeHelper;
    private final GeneralFormService generalFormService;

    @Override
    public ProcessDefinitionResult get(ProcDefQuery procDefQuery) {
//        ProcessDefinition processDefinition = processDefinitionRepository.get(procDefQuery.getFormId());
//        return assembler.toProcessR(processDefinition);
        return null;
    }

    @Override
    @Transactional
    public ProcessDefSaveResult save(ProcessSaveCmd saveCmd) {
        // 如果流程名称为空，则使用流程编码作为名称
        if (saveCmd.getProcessName() == null) {
            saveCmd.setProcessName(saveCmd.getProcessCode());
        }
        // 在流程编码不能以数字开头，在前面加一个下划线
        saveCmd.setProcessCode("_" + saveCmd.getProcessCode());
        FlowNode flowNode = JSONObject.parseObject(saveCmd.getJsonContent(), FlowNode.class);

        // 在每个节点id前加一个下划线_
        flowNodeHelper.idAddUnderline(flowNode);
        // 校验流程
        flowNodeHelper.validateProcessNodes(flowNode);
        // 转成 bpmn20.xml
        String xmlContent = bpmn20Generator.generateXml(saveCmd.getProcessCode(), saveCmd.getProcessName(), flowNode);
        String xmlFileName = saveCmd.getProcessName() + "-bpmn20.xml";
        // 保存流程到 flowable
        FlwDeployCmd flwDeployCmd = FlwDeployCmd.builder().processName(saveCmd.getProcessName())
                .processKey(saveCmd.getProcessCode())
                .processText(xmlContent)
                .resourceName(xmlFileName).build();
//        FlwDeployResult flwDeployResult = flowableExecutor.save(flwDeployCmd);
//
//        MenuBriefResult formBrief = generalFormService.getFormBrief(saveCmd.getFormId());
//        // 保存流程信息到业务表
//        ProcessDefinition processDefinition = ProcessDefinition.builder().formId(saveCmd.getFormId())
//                .formName(formBrief == null ? null : formBrief.getMenuName())
//                .processDefinitionId(flwDeployResult.getProcessId())
//                .version(flwDeployResult.getVersion())
//                .processDefinitionKey(flwDeployResult.getProcessKey())
//                .processDefinitionName(flwDeployResult.getProcessName())
//                .jsonContent(saveCmd.getJsonContent())
//                .xmlContent(xmlContent).build();
//        ProcessDefinition procDef = processDefinitionRepository.save(processDefinition);
//        // 保存原始的流程节点定义
//        processNodeDefService.save(flowNode, saveCmd.getFormId(), flwDeployResult.getProcessId());

//        return ProcessDefSaveResult.builder().processId(procDef.getId()).build();
        return null;
    }

    @Override
    public Boolean getDefinitionId(String formId) {
        return processDefinitionRepository.isDefinitionExist(formId);
    }

}
