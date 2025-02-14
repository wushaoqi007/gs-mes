package com.greenstone.mes.material.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-02-23-10:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatQuery {

    private String startTime;
    private String endTime;
    private Date statisticDate;
    private String provider;
    private String projectCode;
}
