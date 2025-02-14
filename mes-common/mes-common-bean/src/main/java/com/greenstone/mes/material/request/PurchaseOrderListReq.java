package com.greenstone.mes.material.request;

import lombok.Data;

@Data
public class PurchaseOrderListReq {

    private String projectCode;

    /**
     * 进度
     */
    private String status;

    /**
     * 申请开始时间
     */
    private String startTime;

    /**
     * 申请结束时间
     */
    private String endTime;

    /**
     * 零件(物料)id
     */
    private Long id;

    /**
     * code
     */
    private String code;

    /**
     * 版本
     */
    private String version;

}
