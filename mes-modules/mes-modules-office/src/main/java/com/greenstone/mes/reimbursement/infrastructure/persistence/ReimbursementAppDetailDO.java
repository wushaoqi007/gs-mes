package com.greenstone.mes.reimbursement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.reimbursement.infrastructure.enums.ExpenseType;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:59
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("reimbursement_application_detail")
public class ReimbursementAppDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4287590711526124185L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ExpenseType expenseType;
    private LocalDate expenseDate;
    private Double amount;
    private String remark;
}
