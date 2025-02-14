package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderProgressResult {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime orderTime;
    private String id;
    private String serialNo;
    private String requirementSerialNo;
    private String provider;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    private Double unitPrice;
    private Double totalPrice;

    private LocalDate processDeadline;
    private LocalDate planDeadline;
    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    private String designer;
    private String remark;

    private Long receivedNumber;
    private Long checkedNumber;
    private Long inStockNumber;
    private Long outStockNumber;
    private Long waitReceivedNumber;
    private Long waitCheckedNumber;
    private Long reworkingNumber;
    private Long waitSurfaceTreatNumber;
    private Long treatingSurfaceNumber;
    private Long waitInStockNumber;
}
