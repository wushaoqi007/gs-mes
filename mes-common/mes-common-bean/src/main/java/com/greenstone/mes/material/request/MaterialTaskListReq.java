package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class MaterialTaskListReq {

    /**
     * 项目
     */
    private String projectCode;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 参与成员
     */
    private Long memberId;


}
