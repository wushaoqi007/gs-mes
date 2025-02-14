package com.greenstone.mes.market.application.dto;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketAppEditCmd extends FormDataSaveCmd {

    @NotEmpty(message = "请选择需要修改的申请单")
    private String serialNo;

    @NotEmpty(message = "请填写标题")
    private String title;

    @NotNull(message = "请选择期望到货日期")
    private LocalDateTime expectedArrivalTime;

    @NotEmpty(message = "请选择审批人")
    private List<Long> approvers;

    private List<Long> copyTo;

    private String content;

    private ProcessStatus status;

    @NotEmpty(message = "请添加附件")
    private List<Attachment> attachments;

    @Data
    public static class Attachment {
        @NotEmpty(message = "请填写附件名称")
        private String name;
        @NotEmpty(message = "请填写附件路径")
        private String path;
    }

}
