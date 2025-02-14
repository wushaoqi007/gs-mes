package com.greenstone.mes.ces.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CesClearStatusChangeCmd {

    @NotNull(message = "请选择更新后的流程状态")
    private ProcessStatus status;

    @NotEmpty(message = "请选择需要更新状态的单据")
    private List<String> serialNos;

}
