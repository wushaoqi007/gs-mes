package com.greenstone.mes.material.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 物料配置对象 base_material
 *
 * @author gu_renkai
 * @date 2022-01-21
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialAddReq {

    @NotEmpty(message = "common.attribute.validation.code")
    private String code;

    @NotEmpty(message = "common.attribute.validation.name")
    @Length(min = 1, max = 64, message = "common.attribute.validation.name")
    private String name;

    @NotEmpty(message = "common.attribute.validation.version")
    @Length(min = 1, max = 30, message = "common.attribute.validation.version")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "common.attribute.validation.version")
    private String version;

    private String surfaceTreatment;

    private String rawMaterial;

    @NotEmpty(message = "common.attribute.validation.unit")
    @Length(min = 1, max = 6, message = "common.attribute.validation.unit")
    private String unit;

    @NotNull(message = "common.attribute.validation.type")
    private Integer type;

    private String weight;

    private String designer;

    private boolean updateSupport;

}