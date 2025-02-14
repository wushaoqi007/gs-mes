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
 * @date 2023-12-20-14:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineWarehouseIn {
    private String id;
    private String serialNo;
    private Integer operation;
    private ProcessStatus status;
    private LocalDateTime inStockTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String applicant;
    private Long applicantId;
    private String applicantNo;
    private String remark;
    private List<MachineWarehouseInDetail> parts;
}
