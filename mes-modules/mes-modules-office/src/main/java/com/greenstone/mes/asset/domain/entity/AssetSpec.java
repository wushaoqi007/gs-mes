package com.greenstone.mes.asset.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/2/2 16:30
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetSpec {

    private Long id;

    private String typeCode;

    private String templateName;

    private String specification;

    private String unit;

}
