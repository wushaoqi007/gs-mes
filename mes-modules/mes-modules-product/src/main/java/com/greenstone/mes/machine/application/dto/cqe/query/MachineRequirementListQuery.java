package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-12-01-13:19
 */
@Data
public class MachineRequirementListQuery {

    private String projectCode;

    private String serialNo;

    private String status;

    private String applyTimeStart;

    private String applyTimeEnd;

    private String applyBy;

    private Long materialId;

    private Boolean checked;

}
