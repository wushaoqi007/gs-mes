package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-28-14:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineWarehouseOutAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
    @NotNull(message = "出库时间不为空")
    private LocalDateTime outStockTime;
    @NotEmpty(message = "经手人不为空")
    private String sponsor;
    @NotNull(message = "经手人id不为空")
    private Long sponsorId;
    private String sponsorNo;
    @NotEmpty(message = "申请人不为空")
    private String applicant;
    @NotNull(message = "申请人id不为空")
    private Long applicantId;
    private String applicantNo;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String orderSerialNo;
        private String orderDetailId;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        private String requirementSerialNo;
        @NotEmpty(message = "物料id不为空")
        private String materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "出库数量不为空")
        private Long outStockNumber;
        @NotEmpty(message = "仓库编码不为空")
        private String warehouseCode;
        private Integer type;
        private Integer classification;
    }
}
