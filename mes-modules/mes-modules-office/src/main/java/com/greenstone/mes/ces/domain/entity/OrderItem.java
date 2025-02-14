package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单物品
 *
 * @author wushaoqi
 * @date 2023-05-24-9:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {


    private String id;
    
    private String serialNo;
    
    private String applicationSerialNo;
    
    private String applicationItemId;
    
    private Long applicationNum;
    
    private String itemName;
    
    private Long itemNum;

    private String itemCode;

    private Long receivedNum;
    
    private Double unitPrice;
    
    private String purchaseLink;
    
    private String specification;
    
    private String picturePath;
    
    private String unit;
    
    private Double totalPrice;
    
    private String provider;

}
