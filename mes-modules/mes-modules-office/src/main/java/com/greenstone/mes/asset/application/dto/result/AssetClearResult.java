package com.greenstone.mes.asset.application.dto.result;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/22 14:54
 */
@Data
public class AssetClearResult {

    private Long id;

    private String serialNo;

    private Long clearBy;

    private String clearByName;

    private LocalDateTime clearTime;

    private List<AssetR> assets;

}
