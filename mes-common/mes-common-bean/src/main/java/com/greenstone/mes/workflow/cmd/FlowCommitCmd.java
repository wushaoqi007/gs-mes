package com.greenstone.mes.workflow.cmd;


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
public class FlowCommitCmd {

    @NotBlank(message = "业务关键字不能为空")
    private String businessKey;

    @NotNull(message = "申请人id不能为空")
    private Long applyUserId;

    @Valid
    @NotEmpty(message = "属性列表不能为空")
    private List<Attr> attrs;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attr {

        @NotBlank(message = "属性名称不能为空")
        private String name;

        @NotBlank(message = "属性值不能为空")
        private String value;
    }

}
