package com.greenstone.mes.machine.application.dto.event;

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
public class MachineStockE {

    private BaseWarehouse warehouse;

    private String remark;

    private String sponsor;

    private String applicant;
    private String applicantNo;

    private BillOperation operation;

    private StockAction action;


    private List<StockDetail> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockDetail {

        private BaseMaterial material;

        private Long number;

        private Long numberAfterOperation;

        private String orderSerialNo;

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
