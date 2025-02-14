package com.greenstone.mes.material.domain;

import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 出入库记录对象 material_stock_history
 *
 * @author gu_renkai
 * @date 2022-01-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class MaterialStockHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 仓库ID
     */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /**
     * 物料ID
     */
    @Excel(name = "物料ID")
    private Long materialId;

    /**
     * 操作(0:入库 1:出库)
     */
    @Excel(name = "操作(0:入库 1:出库)")
    private Integer operation;

    /**
     * 操作数量
     */
    @Excel(name = "操作数量")
    private Long number;

    /**
     * 操作后仓库总数
     */
    @Excel(name = "操作后仓库总数")
    private Long totalNumber;

    /**
     * 经手人
     */
    @Excel(name = "经手人")
    private String sponsor;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;


}