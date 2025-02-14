package com.greenstone.mes.system.application.dto.query;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:41
 */
@Data
public class ParamQuery {
    private String paramType;
    private String paramName;
    private String status;
    private LocalDate beginTime;
    private LocalDate endTime;
}
