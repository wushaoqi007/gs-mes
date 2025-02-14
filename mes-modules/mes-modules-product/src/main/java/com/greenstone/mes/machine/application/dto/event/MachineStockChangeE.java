package com.greenstone.mes.machine.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineStockChangeE {
    @NotEmpty(message = "单号不为空")
    private String serialNo;

    private String remark;

    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        @NotEmpty(message = "详情id不为空")
        private String id;
        private String serialNo;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        @NotNull(message = "物料id不为空")
        private Long materialId;
        @NotEmpty(message = "零件编码不为空")
        private String partCode;
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        private Long stockNumber;
        @NotNull(message = "变更数量不为空")
        private Long changeNumber;
        @NotEmpty(message = "仓库编码不为空")
        private String warehouseCode;
        @NotNull(message = "仓库id不为空")
        private Long warehouseId;
    }
}
