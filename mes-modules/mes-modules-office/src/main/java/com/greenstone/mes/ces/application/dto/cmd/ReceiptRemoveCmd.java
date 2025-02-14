package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReceiptRemoveCmd {

    @NotNull(message = "请选择收货单")
    private List<String> serialNos;

}
