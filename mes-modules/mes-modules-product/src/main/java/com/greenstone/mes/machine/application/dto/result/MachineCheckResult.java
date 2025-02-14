package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-11-10:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private CheckResultType checkResultType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    private Boolean finished;
    private String remark;
    private List<MachineCheckDetail> parts;

}
