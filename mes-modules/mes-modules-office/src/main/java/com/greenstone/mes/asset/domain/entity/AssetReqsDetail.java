package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/2/6 9:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetReqsDetail {

    private Long assetId;

    private Long assetTypeId;

    private String assetTypeCode;

    private String assetTypeName;

    private String barCode;

    private String name;

    private String sn;

    private Long receiveReceiptId;

}
