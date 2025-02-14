package com.greenstone.mes.machine.interfaces.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineStageStockResp {

    /**
     * 库存id
     */
    private Long stockId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库编号
     */
    private String warehouseCode;

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 库存数量
     */
    private Long number;

    /**
     * 阶段
     */
    private Integer stage;

}
