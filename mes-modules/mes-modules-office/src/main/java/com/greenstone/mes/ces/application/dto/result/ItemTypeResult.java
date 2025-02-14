package com.greenstone.mes.ces.application.dto.result;

import lombok.Data;

/**
 * @author wushaoqi
 * @date 2023-05-22-13:51
 */
@Data
public class ItemTypeResult {

    private String id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;
}
