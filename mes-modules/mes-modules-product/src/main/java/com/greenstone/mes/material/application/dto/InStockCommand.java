package com.greenstone.mes.material.application.dto;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InStockCommand {

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

    private List<InStockMaterial> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InStockMaterial {

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
