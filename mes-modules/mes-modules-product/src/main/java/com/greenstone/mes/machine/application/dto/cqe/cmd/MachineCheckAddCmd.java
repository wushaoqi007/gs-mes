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
 * @date 2023-12-11-10:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCheckAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
    @NotNull(message = "不支持此操作")
    private Integer operation;
    private LocalDateTime checkTime;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "机加工订单号不为空")
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
        @NotNull(message = "数量不为空")
        private Long checkedNumber;
        private String outWarehouseCode;
        private String designer;
        private String ngType;
        private String subNgType;
        private String provider;
    }
}
