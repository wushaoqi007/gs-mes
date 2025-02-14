package com.greenstone.mes.asset.application.dto.result;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:31
 */
@Data
public class AssetTypeListR {

    private String id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;

}
