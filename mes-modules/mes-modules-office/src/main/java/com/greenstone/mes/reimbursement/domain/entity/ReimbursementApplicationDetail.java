package com.greenstone.mes.reimbursement.domain.entity;

import com.greenstone.mes.reimbursement.infrastructure.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReimbursementApplicationDetail {

    private String id;
    private String serialNo;
    private ExpenseType expenseType;
    private LocalDate expenseDate;
    private Double amount;
    private String remark;
    private List<ReimbursementApplicationAttachment> attachments;
}
