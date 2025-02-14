package com.greenstone.mes.bom.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BomQueryResp {

    private Long bomId;

    private String bomCode;

    private String bomName;

    private String bomVersion;

    private String projectCode;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private Integer materialType;

    private String materialVersion;

    private List<Composition> compositions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Composition {

        private Long id;

        private Long materialId;

        private String materialCode;

        private String materialName;

        private Integer materialType;

        private String materialVersion;

        private Long number;

        private String unit;

        private String surfaceTreatment;

        private String rawMaterial;

        private String weight;
    }

}
