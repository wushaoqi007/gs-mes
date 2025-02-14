package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckTakeRecord {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime takeTime;
    private String takeBy;
    private Long takeById;
    private String takeByNo;
    private String sponsor;
    private Long sponsorId;
    private String remark;
    private Boolean signed;
    private String spNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String designer;
    private Long takeNumber;
    private String outWarehouseCode;

}
