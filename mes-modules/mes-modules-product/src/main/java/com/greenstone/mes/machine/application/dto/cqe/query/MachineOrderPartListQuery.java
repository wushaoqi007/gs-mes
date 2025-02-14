package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-12-01-13:19
 */
@Data
public class MachineOrderPartListQuery {

    private String projectCode;

    private String orderSerialNo;

    private String requirementSerialNo;

    private String designer;

    private String part;

    private Long materialId;

}
