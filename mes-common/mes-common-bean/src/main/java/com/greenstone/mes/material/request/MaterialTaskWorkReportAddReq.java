
package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class MaterialTaskWorkReportAddReq {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 工作时长
     */
    private Double takeTime;

    /**
     * 问题描述
     */
    private String description;
}
