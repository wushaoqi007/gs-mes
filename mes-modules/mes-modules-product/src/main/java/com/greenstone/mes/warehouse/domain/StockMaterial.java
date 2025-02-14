package com.greenstone.mes.warehouse.domain;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.enums.StockBehavior;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMaterial {

    /**
     * 仓库动作，出库或者入库
     */
    private StockAction action;

    /**
     * 行为，将单据操作、仓库动作、仓库用途结合确定行为，方便查询展示
     */
    private StockBehavior behavior;

    /**
     * 仓库
     */
    private BaseWarehouse warehouse;

    /**
     * 物料
     */
    private BaseMaterial material;

    /**
     * 操作数量
     */
    private Long number;

    /**
     * 对应订单号
     */
    private String orderSerialNo;

    /**
     * 对应项目号
     */
    private String projectCode;


}
