package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/1/30 15:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetType {

    private Long id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;

    private String idHierarchy;

}
