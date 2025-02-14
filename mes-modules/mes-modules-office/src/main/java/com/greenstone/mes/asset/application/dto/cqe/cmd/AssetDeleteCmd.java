package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/30 15:06
 */
@Data
public class AssetDeleteCmd {

    @NotEmpty(message = "请选择资产")
    private List<String> barCodes;

}
