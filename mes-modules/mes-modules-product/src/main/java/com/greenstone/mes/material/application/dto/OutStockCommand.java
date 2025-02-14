package com.greenstone.mes.material.application.dto;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.enums.StorePlaceAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutStockCommand {

    /**
     * 仓库ID
     */
    private BaseWarehouse warehouse;

    /**
     * 备注
     */
    private String remark;

    /**
     * 经手人
     */
    private String sponsor;

    /**
     * 动作
     */
    private StockAction action;

    /**
     * 操作
     */
    private BillOperation operation;

    /**
     * 库存不足时强制出库，忽略库存不足问题
     */
    private boolean forceOut;

    private boolean outboundAll;

    /**
     * 存放点动作
     */
    private StorePlaceAction storePlaceAction;

    @NotEmpty
    @Valid
    private List<OutStockMaterial> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutStockMaterial {

        private BaseMaterial material;

        private Long number;

        private String worksheetCode;

        private String componentCode;

        private String projectCode;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
