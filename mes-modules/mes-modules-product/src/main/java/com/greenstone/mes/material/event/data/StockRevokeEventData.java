package com.greenstone.mes.material.event.data;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
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
public class StockRevokeEventData {

    private BaseWarehouse warehouse;

    private String remark;

    private String sponsor;

    private BillOperation operation;

    private StockAction action;

    /**
     * 全部出库
     */
    private boolean outboundAll;

    private List<StockDetail> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockDetail {

        private BaseMaterial material;

        private Long number;

        private Long numberAfterOperation;

        private String worksheetCode;

        private String componentCode;

        private List<Integer> revokeStageList;

        private WarehouseStage restoreStage;

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
