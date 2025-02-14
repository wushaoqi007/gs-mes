package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/1 9:13
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AssetClear {

    private Long id;

    private String serialNo;

    private Long clearBy;

    private String clearByName;

    private LocalDateTime clearTime;

    private List<Asset> assets;

    private String remark;

}
