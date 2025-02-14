package com.greenstone.mes.ces.application.dto.event;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-05-23-10:16
 */
@Data
public class ItemTypeRemoveE {
    private Long id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;
}
