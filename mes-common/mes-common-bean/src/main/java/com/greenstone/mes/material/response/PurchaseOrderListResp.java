package com.greenstone.mes.material.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderListResp {

    private Long id;

    private String code;

    private String projectCode;

    private String status;

    private String getAndPurchaseNumber;
    private String getNumber;
    private String purchaseNumber;

    private String isRework;

    private String isChanging;

    private String applyName;

    private String updateBy;

    private Date applyTime;

    private Date updateTime;
    private Date confirmTime;

    /**
     * 备注
     */
    private String remark;

}
