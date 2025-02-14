package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
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
public class MachineCalculate {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String calculateBy;
    private Long calculateById;
    private LocalDateTime applyTime;
    private LocalDateTime confirmTime;
    private String confirmBy;
    private List<MachineCalculateDetail> parts;
}
