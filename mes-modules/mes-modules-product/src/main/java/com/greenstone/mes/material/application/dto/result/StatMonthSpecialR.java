package com.greenstone.mes.material.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-03-01-9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatMonthSpecialR {

    private String month;
    private Integer total;
    private Integer updateNum;
    private Integer urgentNum;
    private Integer repairNum;
    private Integer specialTotal;
    private String specialRate;

}
