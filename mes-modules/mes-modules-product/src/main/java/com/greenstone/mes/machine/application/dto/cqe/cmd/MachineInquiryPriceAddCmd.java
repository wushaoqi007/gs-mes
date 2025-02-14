package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineInquiryPriceAddCmd {
    private String id;
    private String serialNo;
    private Boolean urgent;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "需求申请单号不为空")
        private String requirementSerialNo;
        @NotEmpty(message = "需求申请单详情id不为空")
        private String requirementDetailId;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        private String hierarchy;
        @NotEmpty(message = "物料id不为空")
        private String materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "零件数量不为空")
        private Long partNumber;
        @NotNull(message = "图纸数量不为空")
        private Integer paperNumber;
        @NotNull(message = "已扫描图纸数量不为空")
        private Integer scannedPaperNumber;
    }

}
