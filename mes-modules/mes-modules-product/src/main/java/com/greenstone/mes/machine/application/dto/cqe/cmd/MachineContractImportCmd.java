package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineContractImportCmd {
    private String contractNo;
    private String provider;
    private LocalDate orderTime;
    private Double totalPrice;
    private String remark;
    private String createBy;
    private String submitBy;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String projectCode;
        private String partCodeAndVersion;
        private Long processNumber;
        private Double unitPrice;
        private Double totalPrice;
        private LocalDateTime receiveTime;
        private String remark;
    }
}
