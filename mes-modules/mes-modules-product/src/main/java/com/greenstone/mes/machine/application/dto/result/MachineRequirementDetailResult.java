package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirementDetailResult {
    private Long id;
    private String serialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long perSet;
    private Integer setsNumber;
    private Long processNumber;
    private Integer paperNumber;
    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date printDate;
    private String hierarchy;
    private String designer;
    private String remark;
}
