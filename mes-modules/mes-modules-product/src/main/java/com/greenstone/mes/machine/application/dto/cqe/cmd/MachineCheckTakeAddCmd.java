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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCheckTakeAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
    private LocalDateTime takeTime;
    @NotEmpty(message = "取件人不为空")
    private String takeBy;
    @NotNull(message = "取件人id不为空")
    private Long takeById;
    @NotEmpty(message = "取件人工号不为空")
    private String takeByNo;
    private String sponsor;
    private Long sponsorId;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
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
        @NotNull(message = "取件数量不为空")
        private Long takeNumber;
        @NotEmpty(message = "出库仓库编码不为空")
        private String outWarehouseCode;
        private String designer;
    }
}
