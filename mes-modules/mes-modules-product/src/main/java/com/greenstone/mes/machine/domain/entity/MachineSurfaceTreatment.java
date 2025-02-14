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
 * @date 2023-12-14-11:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineSurfaceTreatment {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String surfaceTreatment;
    private LocalDateTime handleTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String provider;
    private String remark;
    private List<MachineSurfaceTreatmentDetail> parts;
}
