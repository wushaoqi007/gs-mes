package com.greenstone.mes.material.infrastructure.enums;

/**
 * 任务良品问题环节类型
 *
 * @author wushaoqi
 * @date 2022-10-24-13:42
 */
public enum ProblemLinkType {

    // 设计
    DESIGN(1),
    // 品检
    QUALITY_INSPECTION(2),
    //装配
    ASSEMBLING(3);

    private final Integer type;

    ProblemLinkType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
