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
public class StatPartsDataSource {

    private String provider;

    private Date uploadTime;

    private String projectCode;

    private String componentName;

    private String partName;

    private String partCode;

    private String partVersion;

    private String rawMaterial;

    private Integer partNum;

    private Integer paperNumber;

    private Integer deliveryNum;

    private Date deliveryTime;

    private Date processingTime;

    private Date planTime;

    private String designer;

}
