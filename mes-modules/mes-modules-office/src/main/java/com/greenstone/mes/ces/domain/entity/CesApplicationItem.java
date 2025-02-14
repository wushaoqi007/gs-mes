package com.greenstone.mes.ces.domain.entity;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */
@Data
public class CesApplicationItem {

    private String id;
    private String serialNo;
    private String itemName;
    private Long itemNum;
    private String typeName;
    private String purchaseLink;
    private String specification;
    private String picturePath;
    private Double unitPrice;
    private Double estimatedCost;
    private Long readyNum;
    private String itemCode;
    private String unit;
    private Long purchasedNum;
    private Long providedNum;

}
