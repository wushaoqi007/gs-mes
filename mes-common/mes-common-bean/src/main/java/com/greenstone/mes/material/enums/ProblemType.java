package com.greenstone.mes.material.enums;

/**
 * 问题环节类型
 *
 * @author wushaoqi
 * @date 2022-11-02-15:53
 */
public enum ProblemType {

    // 设计
    SJ("1"),
    // 品检
    PJ("2"),
    // 装配
    ZP("3");


    private final String type;

    ProblemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
