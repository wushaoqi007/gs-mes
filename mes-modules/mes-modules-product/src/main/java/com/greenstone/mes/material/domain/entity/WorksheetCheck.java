package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-04-03-11:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksheetCheck {
    private String inspectors;
    private String checkTime;
    private Integer partNum;
    private Integer paperNum;
}
