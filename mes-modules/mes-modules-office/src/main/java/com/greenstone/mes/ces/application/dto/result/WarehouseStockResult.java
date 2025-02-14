package com.greenstone.mes.ces.application.dto.result;

import lombok.Data;

@Data
public class WarehouseStockResult {

    private String itemCode;

    private String itemName;

    private String typeCode;

    private String typeName;

    private String specification;

    private String warehouseCode;

    private String warehouseName;

    private Long number;

    private String unit;

    private String defaultPrice;

    private Long maxSecureStock;

    private Long minSecureStock;

    private String picturePath;

}
