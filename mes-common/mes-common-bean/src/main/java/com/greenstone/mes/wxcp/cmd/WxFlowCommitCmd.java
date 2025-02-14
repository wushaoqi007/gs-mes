package com.greenstone.mes.wxcp.cmd;


import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxFlowCommitCmd {

    @NotBlank(message = "模板id不能为空")
    private String templateId;

    @NotNull(message = "申请人id不能为空")
    private Long applyUserId;

    @Valid
    @NotEmpty
    private List<FlowCommitCmd.Attr> attrs;

}
