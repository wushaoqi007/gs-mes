package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.ces.enums.WarehouseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-06-01-10:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
    private Long id;

    private String parentWarehouseCode;
    private String parentWarehouseName;
    private String idHierarchy;
    private String nameHierarchy;

    private String warehouseCode;

    private String warehouseName;

    private WarehouseStatus status;

}
