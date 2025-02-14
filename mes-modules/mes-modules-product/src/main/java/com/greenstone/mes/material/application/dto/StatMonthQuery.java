package com.greenstone.mes.material.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-02-23-10:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatMonthQuery {

    private String year;
    private String month;
    private String monthStart;
    private String monthEnd;
    private String provider;
    private String projectCode;
    private Integer days;
}
