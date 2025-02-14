package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-11-10:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCheckResultCmd {
    @NotEmpty(message = "质检单号不为空")
    private String checkSerialNo;
    @NotNull(message = "不支持此操作")
    private Integer operation;
    private boolean forceOperation;
    private String remark;
    private String ngType;
    private String subNgType;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "质检单详情id不为空")
        private String checkDetailId;
        @NotEmpty(message = "入库仓库编码不为空")
        private String inWarehouseCode;
        @NotNull(message = "质检数量不为空")
        private Long checkedNumber;
        private String projectCode;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
    }
}
