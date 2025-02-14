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
 * @date 2023-12-18-10:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineReworkRecord {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reworkTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String remark;
    private String orderSerialNo;
    private String orderDetailId;
    private String checkSerialNo;
    private String checkDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    private Long reworkNumber;
    private String warehouseCode;
    private String provider;
}
