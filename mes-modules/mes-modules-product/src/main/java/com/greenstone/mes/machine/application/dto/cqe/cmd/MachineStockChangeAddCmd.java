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
 * @date 2023-12-18-10:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineStockChangeAddCmd {
    private String id;
    private String serialNo;
    private LocalDateTime changeTime;
    private String changedBy;
    private Long changedById;
    private String changedByNo;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
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
        private Long stockNumber;
        @NotNull(message = "变更数量不为空")
        private Long changeNumber;
        @NotEmpty(message = "仓库编码不为空")
        private String warehouseCode;
        private String remark;
    }
}
