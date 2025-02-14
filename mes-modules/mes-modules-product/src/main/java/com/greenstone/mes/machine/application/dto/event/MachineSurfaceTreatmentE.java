package com.greenstone.mes.machine.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineSurfaceTreatmentE {
    @NotEmpty(message = "单号不为空")
    private String serialNo;

    private String remark;

    private String sponsor;
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
        private String checkSerialNo;
        private String checkDetailId;
        private String projectCode;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
        private Long handleNumber;
        private String warehouseCode;
        private String provider;
        private String surfaceTreatment;
    }
}
