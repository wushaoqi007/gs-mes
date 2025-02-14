package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-05-22-10:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemType {

    private Long id;

    private String typeCode;

    private String typeName;

    private String parentTypeCode;

    private String nameHierarchy;

    private String idHierarchy;
}
