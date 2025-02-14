package com.greenstone.mes.asset.application.dto.result;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/2/3 16:49
 */

@Data
public class AssetSpecListR {

    private Long id;

    private String typeCode;

    private String templateName;

    private String specification;

    private String unit;
}
