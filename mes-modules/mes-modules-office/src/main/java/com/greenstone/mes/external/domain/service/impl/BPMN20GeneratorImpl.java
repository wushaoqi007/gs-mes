package com.greenstone.mes.external.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.domain.service.BPMN20Generator;
import com.greenstone.mes.system.enums.FormDataType;
import com.greenstone.mes.system.enums.FormFieldMatchAction;
import com.greenstone.mes.external.domain.entity.node.Assignee;
import com.greenstone.mes.external.domain.entity.node.Condition1;
import com.greenstone.mes.external.domain.entity.node.ConditionGroup;
import com.greenstone.mes.external.domain.entity.node.FlowNode;
import com.greenstone.mes.external.domain.helper.FlowNodeHelper;
import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author gu_renkai
 * @date 2023/2/24 16:02
 */
@AllArgsConstructor
@Slf4j
@Service
public class BPMN20GeneratorImpl implements BPMN20Generator {

    private final FlowNodeHelper flowNodeHelper;

    @Override
    public String generateXml(String flowCode, String flowName, FlowNode flowNode) {
        Document document = DocumentHelper.createDocument();

        Stack<Element> elementStack = new Stack<>();
        Element process = initProcess(document, elementStack, flowCode, flowName);

        analysisNodes(process, flowNode, elementStack);

        return document.asXML();
    }

    private Element initProcess(Document document, Stack<Element> elementStack, String flowCode, String flowName) {
        Element definitions = addDefinitions(document);
        Element process = addProcess(definitions, flowCode, flowName);
        addStartEvent(process);
        FlowNode begin = FlowNode.builder().nodeId("startEvent").title("开始").build();
        elementStack.push(addSequenceFlow(process, begin));
        addEndEvent(process);
        return process;
    }

    private void analysisNodes(Element process, FlowNode flowNode, Stack<Element> elementStack) {
        // 根据流程节点创建 xml 元素
        Element nodeElement = addNodeElement(process, flowNode);
        // 流程节点入栈
        elementStack.push(nodeElement);
        // 若上一个元素是 流程序列，则将 此元素的id 设置为流程序列的目标ID
        if (isNodeAfterSequence(elementStack)) {
            // 更新流程序列中的目标ID，并将其从栈中弹出
            updateTarget(elementStack);
        }

        // 任务发起和抄送节点处理
        if (FlowNodeType.APPLY.itsMe(flowNode) || FlowNodeType.COPY.itsMe(flowNode)) {
            Element applyFlow = addSequenceFlow(process, flowNode);
            elementStack.push(applyFlow);
        }

        // 处理用户任务的分支，分支在流程中存在，但不在UI中展示
        if (FlowNodeType.APPROVE.itsMe(flowNode)) {
            // 用户任务结束之后的排他网关判断
            Element exclusiveGateway = addExclusiveGateway(process);
            // 添加审批动作流程序列，连接用户任务和网关
            addSequenceFlow(process, nodeElement, exclusiveGateway);
            // 添加驳回流程序列，连接网关和驳回结束节点
            addRejectEndFlow(process, exclusiveGateway);
            // 添加通过流程序列
            Element approvedFlow = addApprovedFlow(process, exclusiveGateway);
            elementStack.push(approvedFlow);
        }

        // 处理条件节点
        analysisConditions(process, flowNode, elementStack);
        // 处理子节点
        analysisChildren(process, flowNode, elementStack);
        // 处理没有条件节点和子节点的情况
        noConditionOrChildren(flowNode, elementStack);

        // 处理用户任务的分支后将对应的元素从栈中删除
        if (FlowNodeType.APPROVE.itsMe(flowNode)) {
            elementStack.pop();
        }

        // 任务发起和抄送节点处理完成后将元素从栈中删除
        if (FlowNodeType.APPLY.itsMe(flowNode) || FlowNodeType.COPY.itsMe(flowNode)) {
            elementStack.pop();
        }

        // 当前节点在处理完后出栈
        if (nodeElement != null) {
            elementStack.pop();
        }
    }

    private boolean isNodeAfterSequence(Stack<Element> elementStack) {
        Element currElement = elementStack.lastElement();
        Element lastElement = elementStack.get(elementStack.size() - 2);
        return currElement != null && lastElement != null && "sequenceFlow".equals(lastElement.getName());
    }

    private Element addNodeElement(Element process, FlowNode flowNode) {
        FlowNodeType flowNodeType = flowNode.getType();
        
        return switch (flowNodeType) {
            case APPLY -> addStartApprove(process, flowNode);
            case APPROVE -> addUserApprove(process, flowNode);
            case GATEWAY -> addGateway(process, flowNode);
            case CONDITION -> addCondition(process, flowNode);
            case COPY -> addCopy(process, flowNode);
        };
    }

    private void updateTarget(Stack<Element> elementStack) {
        Element currElement = elementStack.lastElement();
        Element lastElement = elementStack.get(elementStack.size() - 2);

        lastElement.addAttribute("targetRef", currElement.attributeValue("id"));
    }

    private void analysisConditions(Element process, FlowNode flowNode, Stack<Element> elementStack) {
        // 流程序列
        List<FlowNode> conditionNodes = flowNode.getConditionNodes();
        if (CollUtil.isNotEmpty(conditionNodes)) {
            for (FlowNode conditionNode : conditionNodes) {
                if (!FlowNodeType.CONDITION.itsMe(conditionNode)) {
                    throw new ServiceException("流程格式错误，条件分支的第一个节点类型不是条件节点");
                }
                conditionNode.setPrevId(flowNode.getNodeId());
                analysisNodes(process, conditionNode, elementStack);
            }
        }
    }

    private void analysisChildren(Element process, FlowNode flowNode, Stack<Element> elementStack) {
        if (CollUtil.isNotEmpty(flowNode.getChildNodes())) {
            for (FlowNode childNode : flowNode.getChildNodes()) {
                childNode.setPrevId(flowNode.getNodeId());
                analysisNodes(process, childNode, elementStack);
            }
        }
    }

    private void noConditionOrChildren(FlowNode flowNode, Stack<Element> elementStack) {
        if (CollUtil.isEmpty(flowNode.getChildNodes()) && CollUtil.isEmpty(flowNode.getConditionNodes())) {
            Element element = elementStack.lastElement();
            element.addAttribute("targetRef", "approveEnd");
        }
    }

    private void addStartEvent(Element process) {
        Element startEvent = process.addElement("startEvent");
        startEvent.addAttribute("id", "startEvent");
    }

    private void addEndEvent(Element process) {
        Element approveEndEvent = process.addElement("endEvent");
        approveEndEvent.addAttribute("id", "approveEnd");
        Element rejectEndEvent = process.addElement("endEvent");
        rejectEndEvent.addAttribute("id", "rejectEnd");
    }

    private Element addStartApprove(Element process, FlowNode flowNode) {
        Element startApprove = process.addElement("userTask");
        startApprove.addAttribute("id", flowNode.getNodeId());
        startApprove.addAttribute("name", flowNode.getTitle());

        return startApprove;
    }

    private Element addExclusiveGateway(Element process) {
        Element userTask = process.addElement("exclusiveGateway");
        userTask.addAttribute("id", "_" + IdUtil.fastUUID());
        return userTask;
    }

    private Element addGateway(Element process, FlowNode flowNode) {
        Element exclusiveGateway = process.addElement("exclusiveGateway");
        exclusiveGateway.addAttribute("id", flowNode.getNodeId());
        exclusiveGateway.addAttribute("name", flowNode.getTitle());
        return exclusiveGateway;
    }

    private Element addUserApprove(Element process, FlowNode flowNode) {
        // 用户任务
        Element userTask = process.addElement("userTask");
        userTask.addAttribute("id", flowNode.getNodeId());
        userTask.addAttribute("name", flowNode.getTitle());
        if (flowNode.getNodeParams() != null) {
            List<Long> userIds = flowNode.getNodeParams().getAssignees().stream().map(Assignee::getUserId).toList();

            String flowableUserValue = null;
            String flowableUserKey = null;
            String isSequential = null;

            switch (flowNode.getNodeParams().getApproveType()) {
                // 指定成员
                case ASSIGN -> flowableUserValue = CollUtil.join(userIds, ",");
                // 自选
                case SELF_SELECT -> flowableUserValue = "${assignee}";
            }

            switch (flowNode.getNodeParams().getSelectionMode()) {
                // 单选：assignee
                case SINGLE -> {
                    flowableUserKey = "assignee";
                    userTask.addAttribute("flowable:" + flowableUserKey, flowableUserValue);
                }
                // 多选：根据多人审批方式确定
                case MULTI -> {
                    if (flowNode.getNodeParams().getMultiApproveMode() == null){
                        throw new RuntimeException("缺少多人审批方式");
                    }
                    switch (flowNode.getNodeParams().getMultiApproveMode()) {
                        // 会签
                        case ALL -> {
                            flowableUserKey = "assignee";
                            isSequential = "false";
                        }
                        // 依次审批
                        case SUCCESSIVELY -> {
                            flowableUserKey = "assignee";
                            isSequential = "true";
                        }
                        // 或签：candidateUsers
                        case OR -> flowableUserKey = "candidateUsers";
                    }

                    // 处理多人审批
                    switch (flowNode.getNodeParams().getMultiApproveMode()) {
                        // 会签 和 依次审批：多实例任务
                        case ALL, SUCCESSIVELY -> {
                            userTask.addAttribute("flowable:" + flowableUserKey, flowableUserValue);
                            Element multiInstance = process.addElement("multiInstanceLoopCharacteristics");
                            userTask.add(multiInstance);
                            multiInstance.addAttribute("flowable:collection", "assigneeList");
                            multiInstance.addAttribute("flowable:elementVariable", "assignee");
                            multiInstance.addAttribute("isSequential", isSequential);
                        }

                        // 或签
                        case OR -> userTask.addAttribute("flowable:" + flowableUserKey, flowableUserValue);
                    }
                }
            }
        }
        return userTask;
    }

    private void addRejectEndFlow(Element process, Element gateway) {
        Element sequenceFlow = process.addElement("sequenceFlow");
        sequenceFlow.addAttribute("sourceRef", gateway.attributeValue("id"));
        sequenceFlow.addAttribute("targetRef", "rejectEnd");
        Element expression = sequenceFlow.addElement("conditionExpression");
        expression.addAttribute("xsi:type", "tFormalExpression");
        expression.addText("${!approved}");
    }

    private Element addApprovedFlow(Element process, Element gateway) {
        Element sequenceFlow = process.addElement("sequenceFlow");
        sequenceFlow.addAttribute("sourceRef", gateway.attributeValue("id"));
        Element expression = sequenceFlow.addElement("conditionExpression");
        expression.addAttribute("xsi:type", "tFormalExpression");
        expression.addText("${approved}");
        return sequenceFlow;
    }

    private Element addCopy(Element process, FlowNode flowNode) {
        Element userTask = process.addElement("serviceTask");
        userTask.addAttribute("id", flowNode.getNodeId());
        userTask.addAttribute("name", flowNode.getTitle());
        userTask.addAttribute("flowable:class", "com.greenstone.mes.flow.application.delegate.CopyDelegate");
        return userTask;
    }

    private Element addSequenceFlow(Element process, FlowNode flowNode) {
        Element sequenceFlow = process.addElement("sequenceFlow");
        sequenceFlow.addAttribute("sourceRef", flowNode.getNodeId());
        return sequenceFlow;
    }

    private Element addCondition(Element process, FlowNode flowNode) {
        Element sequenceFlow = process.addElement("sequenceFlow");
        sequenceFlow.addAttribute("sourceRef", flowNode.getPrevId());
        // 添加条件表达式
        Element expression = sequenceFlow.addElement("conditionExpression");
        expression.addAttribute("xsi:type", "tFormalExpression");
        expression.addText("${" + elCondition(flowNode) + "}");

        return sequenceFlow;
    }

    private String elCondition(FlowNode flowNode) {
        List<String> groupStr = new ArrayList<>();
        if (flowNode.getNodeParams() == null) {
            throw new ServiceException("条件节点必须设置条件参数");
        }
        for (ConditionGroup conditionGroup : flowNode.getNodeParams().getConditionGroups()) {
            List<String> condStr = new ArrayList<>();
            for (Condition1 condition : conditionGroup.getConditions()) {
                FormDataType formDataType = condition.getFieldType();
                FormFieldMatchAction formFieldMatchAction = condition.getMatchAction();
                String elExpression = flowNodeHelper.elExpression(formFieldMatchAction, formDataType, condition.getFieldCode(), condition.getMatchValue());
                condStr.add(elExpression);
            }
            String groupCond = CollUtil.join(condStr, "&&");
            groupStr.add(groupCond);
        }
        return CollUtil.join(groupStr, "||");
    }

    private void addSequenceFlow(Element process, Element source, Element target) {
        Element sequenceFlow = process.addElement("sequenceFlow");
        sequenceFlow.addAttribute("sourceRef", source.attributeValue("id"));
        sequenceFlow.addAttribute("targetRef", target.attributeValue("id"));
    }

    private Element addProcess(Element definitions, String flowCode, String flowName) {
        Element process = definitions.addElement("process");
        process.addAttribute("id", flowCode);
        process.addAttribute("name", flowName);
        process.addAttribute("isExecutable", "true");
        return process;
    }

    private Element addDefinitions(Document document) {
        Element definitions = document.addElement("definitions", "http://www.omg.org/spec/BPMN/20100524/MODEL");
        definitions.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        definitions.addAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        definitions.addAttribute("xmlns:flowable", "http://flowable.org/bpmn");
        definitions.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        definitions.addAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC");
        definitions.addAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI");
        definitions.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
        definitions.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
        definitions.addAttribute("targetNamespace", "http://www.flowable.org/processdef");
        return definitions;
    }
}
