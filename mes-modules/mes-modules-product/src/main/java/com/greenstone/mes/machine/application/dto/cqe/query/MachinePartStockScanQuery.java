package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-12-08-13:39
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachinePartStockScanQuery {
    @NotEmpty(message = "零件号不为空")
    private String partCode;
    private String partName;
    @NotEmpty(message = "零件版本不为空")
    private String partVersion;
    @NotEmpty(message = "仓库编码不为空")
    private String warehouseCode;
}
