package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderContractResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String provider;
    private LocalDate orderTime;
    private Double totalPrice;
    private String remark;
    private Long totalProcess;
    private Long totalReceived;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<MachineOrderContractDetail> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MachineOrderContractDetail{
        private String id;
        private String serialNo;
        private String requirementSerialNo;
        private String projectCode;
        private String hierarchy;
        private Long materialId;
        private String partCode;
        private String partName;
        private String partVersion;
        private Long processNumber;

        private LocalDate processDeadline;
        private LocalDate planDeadline;
        private String provider;

        private String surfaceTreatment;
        private String rawMaterial;
        private String weight;
        private String designer;
        private String remark;

        @JsonFormat(pattern = "MM月dd日")
        private LocalDateTime receiveTime;
        private Long receivedNumber;

        private Double unitPrice;
        private Double totalPrice;

        private String unit;

    }
}
