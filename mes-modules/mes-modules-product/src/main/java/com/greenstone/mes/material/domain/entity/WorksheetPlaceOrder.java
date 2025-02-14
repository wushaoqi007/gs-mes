package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-03-31-11:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksheetPlaceOrder {
    private String partCode;
    private String partVersion;
    private String partName;
    private Integer placeOrderNum;
    private Integer receiveNum;
    private String projectCode;
    private String designer;
    private Date uploadTime;
    private Date confirmTime;
    private Date receiveTime;
}
