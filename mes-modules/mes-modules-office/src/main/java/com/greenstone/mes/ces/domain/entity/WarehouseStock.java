package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-06-05-13:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStock {
    private Long id;
    private String warehouseCode;
    private String itemCode;
    private Long number;
}
