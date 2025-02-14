package com.greenstone.mes.reimbursement.application.dto;

import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import com.greenstone.mes.reimbursement.infrastructure.enums.ExpenseType;
import com.greenstone.mes.reimbursement.infrastructure.enums.ReimbursementType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReimbursementAppSaveCmd extends FormDataSaveCmd {

    private String serialNo;
    @NotNull(message = "请选择报销类型")
    private ReimbursementType type;
    private String reason;

    @NotEmpty(message = "请选择审批人")
    private String approvedBy;

    @NotNull(message = "审批人id不为空")
    private Long approvedById;

    @Valid
    @NotEmpty(message = "请添加报销明细")
    private List<ReimbursementDetail> details;

    @Data
    public static class ReimbursementDetail {
        @NotNull(message = "请选择费用类型")
        private ExpenseType expenseType;
        @NotNull(message = "请选择发生日期")
        private LocalDate expenseDate;
        @NotNull(message = "请填写费用金额")
        private Double amount;
        private String remark;
        @Valid
        @NotEmpty(message = "请添加附件")
        private List<Attachment> attachments;
    }

    @Data
    public static class Attachment {
        @NotEmpty(message = "请填写附件名称")
        private String name;
        @NotEmpty(message = "请填写附件路径")
        private String path;
        @NotEmpty(message = "请添加附件类型")
        private String attachmentType;
    }

}
