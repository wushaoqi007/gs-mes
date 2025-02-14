package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.infrastructure.enums.UseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-03-14:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineMaterialUse {
    private String id;
    private String serialNo;
    private String materialRequirementSerialNo;
    private ProcessStatus status;
    private UseStatus useStatus;
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
