package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineMaterialUseDetail;
import com.greenstone.mes.machine.infrastructure.enums.UseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-03-9:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineMaterialUseResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private UseStatus useStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String operator;
    private Long operatorId;
    private String operatorNo;
    private String remark;
    private List<MachineMaterialUseDetail> parts;
}
