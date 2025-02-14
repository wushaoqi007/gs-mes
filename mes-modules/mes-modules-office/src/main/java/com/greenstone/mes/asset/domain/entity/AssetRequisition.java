package com.greenstone.mes.asset.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/6 9:33
 */
@Data
public class AssetRequisition {

    private Long id;

    private String serialNo;

    private Long receivedId;

    private String receivedBy;

    private LocalDateTime receivedTime;

    private Long operatedId;

    private String operatedBy;

    private String remark;

    private List<Asset> assets;

}
