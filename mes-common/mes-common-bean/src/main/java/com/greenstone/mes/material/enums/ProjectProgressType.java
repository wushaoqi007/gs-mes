package com.greenstone.mes.material.enums;

/**
 * 项目进度类型（项目、组件）
 */
public enum ProjectProgressType {

    PROJECT("1"),
    COMPONENT("2");


    private String type;

    public String getType() {
        return type;
    }

    ProjectProgressType(String type) {
        this.type = type;
    }

}
