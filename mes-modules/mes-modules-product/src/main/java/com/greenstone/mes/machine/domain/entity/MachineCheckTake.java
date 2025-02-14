
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
public class MachineCheckTake {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime takeTime;
    private String takeBy;
    private Long takeById;
    private String takeByNo;
    private String sponsor;
    private Long sponsorId;
    private String remark;
    private Boolean signed;
    private String spNo;
    private Boolean imported;
    private List<MachineCheckTakeDetail> parts;
}
