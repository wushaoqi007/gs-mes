package com.greenstone.mes.asset.infrastructure.constant;

/**
 * @author gu_renkai
 * @date 2023/2/6 13:54
 */

public interface AssetSnConst {

    String REQUISITION_SN_TYPE = "asset_requisition";

    String REPAIR_SN_TYPE = "asset_repair";

    String REQUISITION_SN_PREFIX = "ARE";

    String REPAIR_SN_PREFIX = "ARP";

    String REVERT_SN_TYPE = "asset_revert";

    String REVERT_SN_PREFIX = "ART";

    String ASSET_SN_TYPE_PREFIX = "asset_type_";

    static String getAssetSnType(String assetType) {
        return ASSET_SN_TYPE_PREFIX + assetType;
    }

}
