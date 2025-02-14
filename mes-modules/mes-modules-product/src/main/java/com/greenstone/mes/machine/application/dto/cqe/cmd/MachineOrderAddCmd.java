package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-11-10:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineOrderAddCmd {
    @NotEmpty(message = "请选择需求单")
    private List<@NotBlank(message = "单号错误") String> serialNos;

}
