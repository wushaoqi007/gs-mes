package com.greenstone.mes.asset.application.dto.cqe.event;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:22
 */
@Data
public class AssetTypeAddE {

    private Long id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;

}
