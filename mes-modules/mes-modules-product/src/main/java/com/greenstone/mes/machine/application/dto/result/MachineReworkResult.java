package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineReworkDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-18-10:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineReworkResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reworkTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String remark;
    private List<MachineReworkDetail> parts;
}
