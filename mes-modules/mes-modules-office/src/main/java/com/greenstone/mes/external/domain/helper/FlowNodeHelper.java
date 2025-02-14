package com.greenstone.mes.external.domain.helper;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.domain.entity.node.FlowNode;
import com.greenstone.mes.external.infrastructure.enums.ApproveType;
import com.greenstone.mes.external.infrastructure.enums.FlowNodeType;
import com.greenstone.mes.system.enums.FormDataType;
import com.greenstone.mes.system.enums.FormFieldMatchAction;
import org.springframework.stereotype.Service;

/**
 * @author gu_renkai
 * @date 2023/3/3 15:18
 */
@Service
public class FlowNodeHelper {

    public void idAddUnderline(FlowNode flowNode) {
        flowNode.setNodeId("_" + flowNode.getNodeId());
        if (CollUtil.isNotEmpty(flowNode.getConditionNodes())) {
            for (FlowNode conditionNode : flowNode.getConditionNodes()) {
                idAddUnderline(conditionNode);
            }
        }
        if (CollUtil.isNotEmpty(flowNode.getChildNodes())) {
            for (FlowNode childNode : flowNode.getChildNodes()) {
                idAddUnderline(childNode);
            }
        }
    }

    public void validateProcessNodes(FlowNode flowNode) {
        if (flowNode.getType() != FlowNodeType.APPLY) {
            throw new ServiceException("流程必须以发起节点开始");
        }
        if (CollUtil.isEmpty(flowNode.getConditionNodes()) && CollUtil.isEmpty(flowNode.getChildNodes())) {
            throw new ServiceException("流程不能只有发起节点");
        }
        validateNodes(flowNode);
    }

    public String elExpression(FormFieldMatchAction action, FormDataType formDataType, String key, Object value) {
        action.validFieldType(formDataType);
        return switch (action) {
            case EQ -> eqEl(formDataType, key, value);
            case NEQ -> neqEl(formDataType, key, value);
            case LT -> ltEl(formDataType, key, value);
            case LTE -> lteEl(formDataType, key, value);
            case GT -> gtEl(formDataType, key, value);
            case GTE -> gteEl(formDataType, key, value);
            case CONTAIN -> containEl(formDataType, key, value);
            case BELONG -> belongEl(formDataType, key, value);
        };
    }

    private String eqEl(FormDataType formDataType, String key, Object value) {
        if (formDataType == FormDataType.NUMBER || formDataType == FormDataType.FLOAT) {
            return key + "==" + value;
        } else {
            return key + "=='" + value + "'";
        }
    }

    private String neqEl(FormDataType formDataType, String key, Object value) {
        if (formDataType == FormDataType.NUMBER || formDataType == FormDataType.FLOAT) {
            return key + "!=" + value;
        } else {
            return key + "!='" + value + "'";
        }
    }

    private String ltEl(FormDataType formDataType, String key, Object value) {
        return key + "<" + value;
    }

    private String lteEl(FormDataType formDataType, String key, Object value) {
        return key + "<=" + value;
    }

    private String gtEl(FormDataType formDataType, String key, Object value) {
        return key + ">" + value;
    }

    private String gteEl(FormDataType formDataType, String key, Object value) {
        return key + ">=" + value;
    }

    private String containEl(FormDataType formDataType, String key, Object value) {
        return key + ".contains('" + value + "')";
    }

    private String belongEl(FormDataType formDataType, String key, Object value) {
        return "'" + value + "'.contains(" + key + ")";
    }

    private void validateNodes(FlowNode flowNode) {
        if (flowNode.getType() == FlowNodeType.GATEWAY && CollUtil.isEmpty(flowNode.getConditionNodes())) {
            throw new ServiceException("分支节点下必须有条件节点");
        }
        if (flowNode.getType() == FlowNodeType.CONDITION && CollUtil.isEmpty(flowNode.getChildNodes())) {
            throw new ServiceException("条件节点下必须有子节点");
        }
        if (flowNode.getType() == FlowNodeType.APPROVE) {
            if (flowNode.getNodeParams() == null){
                throw new ServiceException("审批节点缺少参数");
            }
            if (flowNode.getNodeParams().getApproveType() == ApproveType.ASSIGN  && CollUtil.isEmpty(flowNode.getNodeParams().getAssignees())) {
                throw new ServiceException("审批节点未指定审批人");
            }
        }

        if (CollUtil.isNotEmpty(flowNode.getConditionNodes())) {
            for (FlowNode conditionNode : flowNode.getConditionNodes()) {
                validateNodes(conditionNode);
            }
        }

        if (CollUtil.isNotEmpty(flowNode.getChildNodes())) {
            for (FlowNode childNode : flowNode.getChildNodes()) {
                validateNodes(childNode);
            }
        }
    }

}
