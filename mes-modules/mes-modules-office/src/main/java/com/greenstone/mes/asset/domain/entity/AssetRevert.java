package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:13
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetRevert {

    private Long id;

    private String serialNo;

    private Long revertedId;

    private String revertedBy;

    private LocalDateTime revertedTime;

    private Long operatedId;

    private String operatedBy;

    private String remark;

    List<Asset> assets;
}
