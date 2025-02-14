package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialTaskReportListReq {

    @NotNull(message = "taskId不为空")
    private Long taskId;

}
