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
public class MachineSurfaceTreatmentAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
//    @NotEmpty(message = "处理方式不为空")
    private String surfaceTreatment;
    private LocalDateTime handleTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String provider;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String checkSerialNo;
        private String checkDetailId;
//        @NotEmpty(message = "机加工订单号不为空")
        private String orderSerialNo;
//        @NotEmpty(message = "机加工订单详情id不为空")
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
        private Long processNumber;
        @NotNull(message = "处理数量不为空")
        private Long handleNumber;
        @NotEmpty(message = "出库仓库编码不为空")
        private String warehouseCode;
        private String provider;
    }
}
