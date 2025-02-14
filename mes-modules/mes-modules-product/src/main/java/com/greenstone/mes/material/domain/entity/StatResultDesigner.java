package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 设计月统计
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResultDesigner {


    private Long id;
    
    private String statisticDate;
    
    private String statisticMonth;
    
    private String projectCode;
    
    private Integer partTotal;
    
    private Integer paperTotal;
    
    private Boolean overdue;
    
    private Integer overdueDays;
    
    private Integer partUpdateTotal;
    
    private Integer paperUpdateTotal;
    
    private Integer partUrgentTotal;
    
    private Integer paperUrgentTotal;
    
    private Integer partRepairTotal;
    
    private Integer paperRepairTotal;
}
