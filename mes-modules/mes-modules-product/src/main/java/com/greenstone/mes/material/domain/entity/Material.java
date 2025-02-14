package com.greenstone.mes.material.domain.entity;

import com.greenstone.mes.material.domain.types.MaterialId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2022/10/31 9:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Material {

    private MaterialId id;

    private String name;

    private String code;

    private String version;

    private String unit;

    private String weight;

    private String surfaceTreatment;

    private String rawMaterial;

    private String designer;

    private Integer paperNumber;

}
