package com.greenstone.mes.reimbursement.domain.entity;

import com.greenstone.mes.form.domain.BaseFormDataEntity;
import com.greenstone.mes.reimbursement.infrastructure.enums.ReimbursementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReimbursementApplication extends BaseFormDataEntity {

    @Serial
    private static final long serialVersionUID = 766754840707900967L;
    private ReimbursementType type;
    private String reason;
    private Double total;
    private LocalDateTime appliedTime;
    private String appliedBy;
    private Long appliedById;
    private String appliedByNo;
    private LocalDateTime approvedTime;
    private String approvedBy;
    private Long approvedById;
    private List<ReimbursementApplicationDetail> details;

}
