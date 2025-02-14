package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加工商日统计
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResultDaily {

    private Long id;

    private String statisticDate;

    private String provider;

    private String projectCode;

    private Integer partOweTotal;

    private Integer paperOweTotal;

    private Integer partOweNum;

    private Integer paperOweNum;

    private Integer partDeliveryTotal;

    private Integer paperDeliveryTotal;

    private Integer partDeliveryNum;

    private Integer paperDeliveryNum;

    private Integer partPlanTotal;

    private Integer paperPlanTotal;

    private Integer partPlanNum;

    private Integer paperPlanNum;

    private Integer partOverdueNum;

    private Integer paperOverdueNum;

    private Integer partOverdueThreeDaysNum;

    private Integer paperOverdueThreeDaysNum;

    private Integer partReworkNum;

    private Integer paperReworkNum;

    private Integer partCheckNum;

    private Integer paperCheckNum;
}
