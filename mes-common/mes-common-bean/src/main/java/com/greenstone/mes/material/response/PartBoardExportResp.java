package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartBoardExportResp {

    private Long id;

    private Long purchaseOrderId;

    /**
     * 加工单位
     */
    private String provider;

    /**
     * 生产代码
     */
    private String projectCode;

    /**
     * 机种名称
     */
    private String componentName;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 零件编号
     */
    private String code;

    /**
     * 零件版本
     */
    private String version;

    /**
     * 材料
     */
    private String rawMaterial;

    /**
     * 是否加急
     */
    private String isFast;

    /**
     * 订单数量
     */
    private Long materialNumber;

    /**
     * 收货数量
     */
    private Integer getNumber;

    /**
     * 收货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date receivingTime;

    /**
     * 加工纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processingTime;

    /**
     * 计划纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planTime;

    /**
     * 设计
     */
    private String designer;


    /**
     * 入库日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inStockTime;

    /**
     * 零件生成日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 入库数量
     */
    private Integer inStockNumber;

    private String buyLimit;

    private Integer paperNumber;

    /**
     * 表面处理
     */
    private String surfaceTreatment;

    private String weight;

    /**
     * 物料id
     */
    private Long materialId;

    /**
     * 收货是否存在超期
     */
    private String receiveDelay;

    /**
     * 入库是否存在超期
     */
    private String inStockDelay;

    /**
     * 工序是否存在超期
     */
    private String workProcedureDelay;


}
