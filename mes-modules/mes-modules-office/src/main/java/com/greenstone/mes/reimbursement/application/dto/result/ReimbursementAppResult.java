package com.greenstone.mes.reimbursement.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplicationDetail;
import com.greenstone.mes.reimbursement.infrastructure.enums.ReimbursementType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReimbursementAppResult {

    private String id;
    private String serialNo;
    private ProcessStatus status;
    private ReimbursementType type;
    private String reason;
    private Double total;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime appliedTime;
    private String appliedBy;
    private Long appliedById;
    private String appliedByNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime submitTime;
    private String submitBy;
    private Long submitById;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime approvedTime;
    private String approvedBy;
    private Long approvedById;
    private List<ReimbursementApplicationDetail> details;

}
