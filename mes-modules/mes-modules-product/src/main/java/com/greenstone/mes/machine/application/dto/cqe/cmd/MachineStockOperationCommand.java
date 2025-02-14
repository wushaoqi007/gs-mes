package com.greenstone.mes.machine.application.dto.cqe.cmd;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/14 9:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineStockOperationCommand {
    @NotNull(message = "不支持此操作")
    private BillOperation operation;
    private StockAction action;
    private BaseWarehouse warehouse;
    private BaseWarehouse toWarehouse;
    @NotEmpty(message = "经手人不能为空")
    private String sponsor;
    private String applicant;
    private String applicantNo;
    private String remark;

    private NgData ngData;

    @Valid
    private List<TransferMaterial> materialList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransferMaterial {
        @NotNull(message = "物料不能为空")
        private BaseMaterial material;
        @NotNull(message = "物料数量不能为空")
        private Long number;
        @NotEmpty(message = "项目代码不能为空")
        private String projectCode;
//        @NotEmpty(message = "机加工订单号不能为空")
        private String orderSerialNo;
//        @NotEmpty(message = "机加工订单详情id不能为空")
        private String orderDetailId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NgData {
        private String ngType;
        private String subNgType;
    }
}
