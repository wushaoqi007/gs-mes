package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-12-29-13:39
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineOrderPartChooseQuery {
    private String serialNo;
    private String projectCode;
    private String partCode;
    private String partName;
    private String partVersion;
    private String warehouseCode;
}
