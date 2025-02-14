package com.greenstone.mes.machine.application.dto.event;

import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
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
public class MachineCheckE {
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    private String checkBy;
    @NotNull(message = "检验类型不为空")
    private CheckResultType checkResultType;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        @NotEmpty(message = "详情id不为空")
        private String id;
        private String serialNo;
        private String orderSerialNo;
        private String orderDetailId;
        private String projectCode;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
        private String designer;
        private Long processNumber;
        private Long checkedNumber;
        private Long toBeCheckedNumber;
        private String outWarehouseCode;
        private CheckResultType checkResultType;
        private CheckResult checkResult;
        private String ngType;
        private String subNgType;
        private String inWarehouseCode;
    }
}
