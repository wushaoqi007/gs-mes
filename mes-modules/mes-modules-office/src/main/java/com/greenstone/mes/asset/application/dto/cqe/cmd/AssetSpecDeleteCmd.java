package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author gu_renkai
 * @date 2023/2/7 15:50
 */
@Data
public class AssetSpecDeleteCmd {

    @NotNull(message = "请选择型号规格")
    private Long id;


}
