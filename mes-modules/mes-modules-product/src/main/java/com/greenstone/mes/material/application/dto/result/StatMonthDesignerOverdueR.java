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
public class StatMonthDesignerOverdueR {

    private String month;
    private Integer planNum;
    private Integer actualNum;
    private Integer overdueNum;
    private String overdueRate;

}
