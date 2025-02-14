package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialComplaintRecordEditReq {

    @NotNull(message = "material.complaint.record.id")
    private Long id;

    /**
     * 检查人
     */
    private String inspector;

    /**
     * 问题环节类型
     */
    @NotNull(message = "material.complaint.record.problemType")
    private Integer problemType;

}
