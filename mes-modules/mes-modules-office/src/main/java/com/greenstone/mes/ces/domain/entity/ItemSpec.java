package com.greenstone.mes.ces.domain.entity;

import com.greenstone.mes.ces.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemSpec {
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
