package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.greenstone.mes.external.domain.entity.node.FlowNode;

/**
 * @author gu_renkai
 * @date 2023/2/24 14:15
 */

public enum FlowNodeType {
    APPLY(0, "apply", "申请"),
    APPROVE(1, "approve", "审批"),
    GATEWAY(2, "gateway", "网关"),
    CONDITION(3, "condition", "条件"),
    COPY(4, "copy", "抄送"),
    ;
    @EnumValue
    @JSONField
    private final int id;

    private final String code;

    private final String name;

    FlowNodeType(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean itsMe(FlowNode node) {
        return this == node.getType();
    }

    public boolean itsMe(int type) {
        return id == type;
    }

    public static FlowNodeType fromId(int id) {
        for (FlowNodeType value : FlowNodeType.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
