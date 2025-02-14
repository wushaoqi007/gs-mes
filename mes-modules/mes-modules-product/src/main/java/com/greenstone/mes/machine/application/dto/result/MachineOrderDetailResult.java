package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderDetailResult {
    private String id;
    private String serialNo;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;
    private Long receivedNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inStockTime;
    private Long inStockNumber;
    private LocalDate processDeadline;
    private LocalDate planDeadline;
    private String provider;

    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date printDate;
    private String hierarchy;
    private String designer;
    private String remark;
}
