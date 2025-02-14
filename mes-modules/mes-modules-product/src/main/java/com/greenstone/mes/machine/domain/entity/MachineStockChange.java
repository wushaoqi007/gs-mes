package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-18-11:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineStockChange {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime changeTime;
    private String changedBy;
    private Long changedById;
    private String changedByNo;
    private String remark;
    private List<MachineStockChangeDetail> parts;
}
