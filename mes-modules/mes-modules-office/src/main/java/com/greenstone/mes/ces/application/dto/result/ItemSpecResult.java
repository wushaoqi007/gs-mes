package com.greenstone.mes.ces.application.dto.result;

import com.greenstone.mes.ces.enums.ItemStatus;
import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-05-22-15:25
 */
@Data
public class ItemSpecResult {
    private Long id;

    private String typeCode;

    private String typeName;

    private String itemCode;

    private String itemName;

    private ItemStatus status;

    private String specification;

    private String unit;

    private String barCode;

    private String brand;

    private Double defaultPrice;

    private Long maxSecureStock;

    private Long minSecureStock;

    private String remark;

    private String needReturn;

    private String lossRatePerYear;
}
