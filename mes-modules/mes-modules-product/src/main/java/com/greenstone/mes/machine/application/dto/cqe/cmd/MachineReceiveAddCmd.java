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
public class MachineReceiveAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
    @NotEmpty(message = "加工单位不为空")
    private String provider;
    private LocalDateTime receiveTime;
    private String receiver;
    private Long receiverId;
    private String receiverNo;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotNull(message = "不支持此操作")
        private Integer operation;
        @NotEmpty(message = "机加工订单号不为空")
        private String orderSerialNo;
//        @NotEmpty(message = "机加工订单详情id不为空")
        private String orderDetailId;
        private String surfaceTreatmentSerialNo;
        private String reworkSerialNo;
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
        private Long expectedNumber;
        @NotNull(message = "实到数量不为空")
        private Long actualNumber;
        @NotEmpty(message = "仓库编码不为空")
        private String warehouseCode;
    }

}
