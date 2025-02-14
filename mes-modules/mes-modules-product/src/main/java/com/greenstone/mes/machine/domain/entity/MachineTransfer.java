package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineTransfer {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private WarehouseStage stage;
    private LocalDateTime transferTime;
    private String transferBy;
    private Long transferById;
    private String transferByNo;
    private String remark;
    private List<MachineTransferDetail> parts;
}
