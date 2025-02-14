package com.greenstone.mes.bom.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class BomAddReq {

    @NotEmpty(message = "common.attribute.validation.code")
    @Length(min = 1, max = 30, message = "common.attribute.validation.code")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "common.attribute.validation.code")
    private String code;

    @NotEmpty(message = "common.attribute.validation.name")
    @Length(min = 1, max = 30, message = "common.attribute.validation.name")
    private String name;

    @NotEmpty(message = "common.attribute.validation.version")
    @Length(min = 1, max = 30, message = "common.attribute.validation.version")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "common.attribute.validation.version")
    private String version;

    @Length(max = 30, message = "common.attribute.validation.projectCode")
    private String projectCode;

    private Long materialId;

    @NotEmpty(message = "bom.bom.add.error.lack.material")
    private List<Composition> compositions;

    @Data
    public static class Composition {

        @NotNull(message = "bom.bom.add.error.lack.material.id")
        private Long materialId;

        @Max(value = 999999, message = "common.attribute.validation.amount.max")
        private Long number;
    }

}
