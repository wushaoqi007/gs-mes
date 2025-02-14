package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartsBoardExportResp {

    /**
     * 加工单位
     */
    @Excel(name = "加工单位")
    private String provider;

    /**
     * 生产代码
     */
    @Excel(name = "生产代码")
    private String projectCode;

    /**
     * 机种名称
     */
    @Excel(name = "机种名称")
    private String componentName;

    /**
     * 零件名称
     */
    @Excel(name = "零件名称")
    private String name;

    /**
     * 材料
     */
    @Excel(name = "材料")
    private String rawMaterial;

    /**
     * 是否加急
     */
    @Excel(name = "是否加急")
    private String isFast;

    /**
     * 订单数量
     */
    @Excel(name = "订单数量")
    private Long materialNumber;

    /**
     * 收货数量
     */
    @Excel(name = "收货数量")
    private Integer getNumber;

    /**
     * 收货日期
     */
    @Excel(name = "收货日期", dateFormat = "yyyy-MM-dd")
    private Date receivingTime;

    /**
     * 加工纳期
     */
    @Excel(name = "加工纳期", dateFormat = "yyyy-MM-dd")
    private Date processingTime;

    /**
     * 计划纳期
     */
    @Excel(name = "计划纳期", dateFormat = "yyyy-MM-dd")
    private Date planTime;

    /**
     * 设计
     */
    @Excel(name = "设计")
    private String designer;

    /**
     * 当前工序列表
     */
    @Excel(name = "当前工序")
    private List<WorkProcedure> WorkProcedureList;


    /**
     * 工序
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WorkProcedure {
        /**
         * 工序名称
         */
        private String name;

        /**
         * 工序时长
         */
        private Long duration;
    }

    /**
     * 入库日期
     */
    @Excel(name = "入库日期", dateFormat = "yyyy-MM-dd")
    private Date inStockTime;

    /**
     * 入库数量
     */
    @Excel(name = "入库数量")
    private Integer inStockNumber;


    /**
     * 表面处理
     */
    private String surfaceTreatment;


}
