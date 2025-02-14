package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartsBoardListReq {


    /**
     * 加工单位(供应商)
     */
    private String provider;

    /**
     * 加工纳期（筛选出零件加工纳期日之前的未交货零件）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processingTime;

    /**
     * 生产代码（项目代码）
     */
    private String projectCode;

    /**
     * 表面处理
     */
    private String surfaceTreatment;

    /**
     * 是否加急
     */
    private String isFast;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 当前工序名称
     */
    private String workProcedureName;

    /**
     * 工序是否超时
     */
    private String isDelay;


}
