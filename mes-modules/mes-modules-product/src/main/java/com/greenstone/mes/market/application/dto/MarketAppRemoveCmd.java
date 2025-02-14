package com.greenstone.mes.market.application.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */

@Data
public class MarketAppRemoveCmd {

    @NotNull(message = "请选择申请单")
    private List<String> serialNos;

}
