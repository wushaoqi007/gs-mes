package com.greenstone.mes.external.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessCmd {

    private String comment;

    private boolean approved;

    @NotEmpty(message = "请选择要审批的单据")
    private List<Runner> runners;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Runner {

        @NotEmpty(message = "单据信息不完整")
        private String taskId;

        @NotEmpty(message = "单据信息不完整")
        private String processInstanceId;
    }

}
