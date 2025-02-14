package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-20-14:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineWarehouseInRecord {
    private String id;
    private String serialNo;
    private Integer operation;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inStockTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String applicant;
    private Long applicantId;
    private String applicantNo;
    private String remark;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String warehouseCode;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long inStockNumber;
}
