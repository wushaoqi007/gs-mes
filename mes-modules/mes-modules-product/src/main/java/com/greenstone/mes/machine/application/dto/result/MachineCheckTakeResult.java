package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineCheckTakeDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckTakeResult {
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
    private Boolean imported;
    private List<MachineCheckTakeDetail> parts;

}
