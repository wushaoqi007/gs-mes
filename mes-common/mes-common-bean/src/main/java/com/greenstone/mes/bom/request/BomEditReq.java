package com.greenstone.mes.bom.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class BomEditReq {

    @NotNull(message = "common.attribute.validation.id")
    private Long id;

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

        @Max(value = 999999, message = "数量最大为999999")
        private Long number;
    }

}
