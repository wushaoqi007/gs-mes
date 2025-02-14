package com.greenstone.mes.asset.application.dto.cqe.event;

import com.greenstone.mes.asset.domain.entity.Asset;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:22
 */
@Data
public class AssetRevertE {

    private Long billId;

    private String serialNo;

    private Long revertedId;

    private String revertedBy;

    private LocalDateTime revertedTime;

    private Long operatedId;

    private String operatedBy;

    private String remark;

    private List<Asset> assets;

    private String changeContent;

}
