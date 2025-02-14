package com.greenstone.mes.machine.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-20-14:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineWarehouseOutE {
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    private String sponsor;
    private String remark;
    private String applicant;
    private String applicantNo;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        @NotEmpty(message = "机加工订单号不为空")
        private String orderSerialNo;
        @NotEmpty(message = "机加工订单详情id不为空")
        private String orderDetailId;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        @NotNull(message = "物料id不为空")
        private Long materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "出库数量不为空")
        private Long outStockNumber;
        @NotNull(message = "仓库id不为空")
        private Long warehouseId;
        private String warehouseCode;
        private String warehouseName;
        private Integer type;
        private Integer classification;
        private String remark;
    }
}
