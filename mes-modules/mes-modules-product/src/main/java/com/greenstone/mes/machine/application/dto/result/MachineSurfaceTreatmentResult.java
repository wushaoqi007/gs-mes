package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-14-10:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineSurfaceTreatmentResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String surfaceTreatment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String provider;
    private String remark;
    private List<MachineSurfaceTreatmentDetail> parts;

}
