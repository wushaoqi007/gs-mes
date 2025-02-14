package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 加工商日统计数据来源
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDataDaily {

    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String componentCode;
    private String componentName;

    private String provider;

    private String projectCode;

    private Integer stage;
    private Integer stageOperation;

    private Date processingTime;

    private Integer paperNumber;

    private Long partNumber;

    private Integer receiveNumber;

    private Integer stockNum;

    private Integer inStockTotal;

    private Integer outStockTotal;

    private Date firstInTime;

    private Date lastInTime;

    private Date firstOutTime;

    private Date lastOutTime;

    private Date createTime;
    private String rawMaterial;
    private String designer;
    private Date planTime;

    private Date uploadTime;
    private Date inStockTime;
    private Date confirmTime;


}
