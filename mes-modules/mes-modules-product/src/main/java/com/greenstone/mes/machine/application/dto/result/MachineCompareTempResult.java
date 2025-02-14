package com.greenstone.mes.machine.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author wushaoqi
 * @date 2023-11-24-13:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCompareTempResult {

    private String id;
    private String requirementId;
    private String requirementSerialNo;
    private String requirementDetailId;
    private String partType;
    private Boolean urgent;
    private String provider;
    private LocalDate processDeadline;
    private LocalDate planDeadline;
    private Integer scannedPaperNumber;
}
