package com.greenstone.mes.reimbursement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import com.greenstone.mes.reimbursement.infrastructure.enums.ReimbursementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:40
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName("reimbursement_application")
public class ReimbursementAppDO extends BaseFormPo {
    @Serial
    private static final long serialVersionUID = -4946434221305764597L;

    private ReimbursementType type;
    private String reason;
    private Double total;
    /**
     * 申请时间
     */
    private LocalDateTime appliedTime;
    private String appliedBy;
    private Long appliedById;
    private String appliedByNo;
    /**
     * 审核时间
     */
    private LocalDateTime approvedTime;
    private String approvedBy;
    private Long approvedById;

    @TableField(exist = false)
    private String dataJson;
    @TableField(exist = false)
    private String processInstanceId;
    @TableField(exist = false)
    private String processDefinitionId;

}
