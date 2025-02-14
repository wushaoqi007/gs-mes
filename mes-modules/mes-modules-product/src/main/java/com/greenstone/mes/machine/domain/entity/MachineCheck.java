package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-11-11:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineCheck  {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private CheckResultType checkResultType;
    private LocalDateTime checkTime;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    private Boolean finished;
    private String remark;
    private List<MachineCheckDetail> parts;
}
