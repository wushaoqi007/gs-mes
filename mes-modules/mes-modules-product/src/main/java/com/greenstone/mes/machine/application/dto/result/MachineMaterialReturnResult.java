package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.domain.entity.MachineMaterialReturnDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-02-8:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineMaterialReturnResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
