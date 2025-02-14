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
 * @date 2024-01-02-14:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineMaterialReturn {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime returnTime;
    private String returnBy;
    private Long returnById;
    private String returnByNo;
    private String operator;
    private Long operatorId;
    private String operatorNo;
    private String remark;
    private List<MachineMaterialReturnDetail> parts;
}
