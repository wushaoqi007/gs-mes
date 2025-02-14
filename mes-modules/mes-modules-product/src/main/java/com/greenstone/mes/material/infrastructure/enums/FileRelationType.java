package com.greenstone.mes.material.infrastructure.enums;

/**
 * 文件上传业务关联类型
 *
 * @author wushaoqi
 * @date 2022-10-24-13:42
 */
public enum FileRelationType {

    // 进度报告
    PROGRESS_REPORT(1),
    // 问题报告
    PROBLEM_REPORT(2),
    //质检结果
    QUALITY_INSPECTION_RESULT(3),
    // 良品问题
    GOOD_PROBLEM(4);

    private final Integer type;

    FileRelationType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
