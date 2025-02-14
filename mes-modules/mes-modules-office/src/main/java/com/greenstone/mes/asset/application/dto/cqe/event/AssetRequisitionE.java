package com.greenstone.mes.asset.application.dto.cqe.event;

import com.greenstone.mes.asset.domain.entity.AssetReqsDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:22
 */
@Data
public class AssetRequisitionE {

    private Long billId;

    private String serialNo;

    private Long receivedId;

    private String receivedBy;

    private LocalDateTime receivedTime;

    private Long operatedId;

    private String operatedBy;

    private String remark;

    private List<AssetReqsDetail> assets;

    private String changeContent;

}
