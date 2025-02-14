package com.greenstone.mes.machine.application.dto.event;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineMaterialUseE {
    @NotNull(message = "领用时间不为空")
    private LocalDateTime useTime;
    @NotEmpty(message = "经手人不为空")
    private String sponsor;
    @NotNull(message = "经手人id不为空")
    private Long sponsorId;
    private String sponsorNo;
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
        @NotNull(message = "领用数量不为空")
        private Long useNumber;
        @NotNull(message = "仓库编码不为空")
        private String warehouseCode;
    }
}
