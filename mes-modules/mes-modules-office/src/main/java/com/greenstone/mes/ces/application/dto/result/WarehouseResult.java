package com.greenstone.mes.ces.application.dto.result;

import com.greenstone.mes.ces.enums.WarehouseStatus;
import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-06-01-15:25
 */
@Data
public class WarehouseResult {
    private Long id;

    private String parentWarehouseCode;
    private String parentWarehouseName;
    private String idHierarchy;
    private String nameHierarchy;

    private String warehouseCode;

    private String warehouseName;

    private WarehouseStatus status;

}
