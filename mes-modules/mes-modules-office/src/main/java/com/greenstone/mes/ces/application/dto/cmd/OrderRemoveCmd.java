package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderRemoveCmd {

    @NotNull(message = "请选择订单")
    private List<String> serialNos;

}
