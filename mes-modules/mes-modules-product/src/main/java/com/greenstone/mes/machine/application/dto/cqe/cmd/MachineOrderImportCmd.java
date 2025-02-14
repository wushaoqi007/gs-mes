package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineOrderImportCmd {
    @Valid
    @NotEmpty(message = "无法导入空数据")
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String serialNo;
        private String inquiryPriceSerialNo;
        private String requirementSerialNo;
        private String projectCode;
        private LocalDate orderTime;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
        private Long processNumber;

        private Long receivedNumber;
        private LocalDateTime receiveTime;

        private LocalDate processDeadline;
        private LocalDate planDeadline;
        private String provider;

        private String surfaceTreatment;
        private String rawMaterial;
        private String weight;
        private String hierarchy;
        private String designer;
        private String remark;

        private BigDecimal unitPrice;
    }

}
