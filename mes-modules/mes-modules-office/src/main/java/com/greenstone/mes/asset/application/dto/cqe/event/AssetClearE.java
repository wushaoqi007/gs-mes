package com.greenstone.mes.asset.application.dto.cqe.event;

import com.greenstone.mes.asset.domain.entity.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:22
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetClearE {

    private Long billId;

    private String serialNo;

    private Long clearBy;

    private String clearByName;

    private LocalDateTime clearTime;

    private List<Asset> assets;

    private boolean restore;

    private String changeContent;

}
