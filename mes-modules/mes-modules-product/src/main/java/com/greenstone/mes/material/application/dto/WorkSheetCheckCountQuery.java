package com.greenstone.mes.material.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-04-03-11:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSheetCheckCountQuery {

    private String inspectors;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不为空")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不为空")
    private Date endTime;

}
