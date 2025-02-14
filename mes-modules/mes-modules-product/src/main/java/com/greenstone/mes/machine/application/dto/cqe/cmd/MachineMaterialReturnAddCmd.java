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
public class MachineMaterialReturnAddCmd {
    private String id;
    private String serialNo;
    @NotNull(message = "退料时间不为空")
    private LocalDateTime returnTime;
    @NotEmpty(message = "退料人不为空")
    private String returnBy;
    @NotNull(message = "退料人id不为空")
    private Long returnById;
    private String returnByNo;
    @NotEmpty(message = "操作人不为空")
    private String operator;
    @NotNull(message = "操作人id不为空")
    private Long operatorId;
    private String operatorNo;
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
        @NotEmpty(message = "机加工订单详情id不为空")
        private String orderDetailId;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        @NotEmpty(message = "物料id不为空")
        private String materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "退回数量不为空")
        private Long returnNumber;
        private String warehouseCode;
    }
}
