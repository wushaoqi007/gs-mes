package com.greenstone.mes.material.dto.cmd;

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
public class StatProgressCmd {

    private String startTime;
    private String endTime;
    private Integer time;
    private String unit;
}
