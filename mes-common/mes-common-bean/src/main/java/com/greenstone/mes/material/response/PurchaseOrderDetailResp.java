package com.greenstone.mes.material.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderDetailResp {

    private Long id;

    private String worksheetCode;

    private Long purchaseOrderId;

    private String projectCode;

    private String componentCode;

    private String componentName;

    private String codeVersion;

    private String name;

    private String buyLimit;

    private Long materialNumber;

    private Integer paperNumber;

    private String rawMaterial;

    private String surfaceTreatment;

    private String weight;

    private Integer getNumber;

    /**
     * 进度
     */
    private String status;

    /**
     * 是否采购
     */
    private String isPurchase;

    /**
     * 是否加急
     */
    private String isFast;

    /**
     * 加工单位
     */
    private String provider;

    /**
     * 加工纳期
     */
    private Date processingTime;

    /**
     * 计划纳期
     */
    private Date planTime;

    /**
     * 类型:加工件、标准件
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

}
