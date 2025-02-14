package com.greenstone.mes.asset.application.dto.result;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:13
 */
@Data
public class AssetRevertR {

    private Long id;

    private String serialNo;

    private Long revertedId;

    private String revertedBy;

    private LocalDateTime revertedTime;

    private Long operatedId;

    private String operatedBy;

    private String remark;

    List<AssetR> assets;
}
