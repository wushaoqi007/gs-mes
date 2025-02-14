package com.greenstone.mes.machine.application.dto.cqe.cmd;

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
public class MachineTransferStockCommand {

    /**
     * 出库仓库
     */
    private BaseWarehouse warehouseOut;

    /**
     * 入库仓库
     */
    private BaseWarehouse warehouseIn;

    /**
     * 备注
     */
    private String remark;

    /**
     * 经手人
     */
    private String sponsor;

    private String applicant;
    private String applicantNo;

    /**
     * 动作
     */
    private StockAction action;

    /**
     * 操作
     */
    private BillOperation operation;

    private List<TransferMaterial> materialList;

    private NgData ngData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferMaterial {

        private BaseMaterial material;

        private Long number;

        private String orderSerialNo;

        private String orderDetailId;

        private String projectCode;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NgData {
        private String ngType;
        private String subNgType;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
