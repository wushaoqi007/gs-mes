package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2023-05-22-15:16
 */
@Data
public class ItemSpecEditCmd {
    @NotNull(message = "请选择型号规格")
    private Long id;

    private String itemCode;

    @NotEmpty(message = "请填写物品名称")
    private String itemName;

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
