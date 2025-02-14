package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-01-13:19
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MachineRequirementPartQuery {

    private String projectCode;

    private String requirementSerialNo;

    private String part;


}
