package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 加工商不良类型统计
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResultRework {


    private Long id;

    private String statisticDate;

    private String provider;

    private String projectCode;

    private String ngType;

    private String subNgType;

    private Integer total;
}
