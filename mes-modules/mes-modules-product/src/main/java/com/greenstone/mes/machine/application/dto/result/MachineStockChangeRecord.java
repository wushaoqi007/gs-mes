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
public class MachineStockChangeRecord {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changeTime;
    private String changedBy;
    private Long changedById;
    private String changedByNo;
    private String remark;
    private String projectCode;
    private Long materialId;
    private Long warehouseId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long stockNumber;
    private Long changeNumber;
    private String warehouseCode;
}
