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
public class MachineTransferAddCmd {
    private String id;
    private String serialNo;
    private boolean forceOperation;
    @NotNull(message = "调拨时间不为空")
    private LocalDateTime transferTime;
    @NotEmpty(message = "调拨人不为空")
    private String transferBy;
    @NotNull(message = "调拨人id不为空")
    private Long transferById;
    private String transferByNo;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "物料id不为空")
        private String materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "数量不为空")
        private Long number;
        @NotEmpty(message = "出库仓库编码不为空")
        private String outWarehouseCode;
        @NotEmpty(message = "入库仓库编码不为空")
        private String inWarehouseCode;
        private String remark;
    }
}
