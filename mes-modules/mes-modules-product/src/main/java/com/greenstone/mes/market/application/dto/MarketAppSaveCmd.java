package com.greenstone.mes.market.application.dto;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketAppSaveCmd extends FormDataSaveCmd {

    private String serialNo;

    @NotNull(message = "请选择期望到货日期")
    private LocalDateTime expectedArrivalTime;

    @NotEmpty(message = "请填写标题")
    private String title;

    @NotEmpty(message = "请填写内容")
    private String content;

    private ProcessStatus status;

    @NotEmpty(message = "请选择审批人")
    private List<Long> approvers;

    @Size(max = 9, message = "抄送人不超过9人")
    private List<Long> copyTo;

    @Valid
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
