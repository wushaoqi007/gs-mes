package com.greenstone.mes.bom.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class BomCompareExportReq {

    @NotNull(message = "请选择组件")
    private Long componentId;

    @Valid
    @NotEmpty(message = "请扫描零件")
    private List<PurchaseMaterial> materials;

    @Getter
    @Setter
    public static class PurchaseMaterial {

        @NotNull(message = "请选择零件")
        private Long materialId;

        @NotBlank(message = "请填写供应商")
        private String provider;

        @NotBlank(message = "请填写纳期")
        private String deliveryTime;

    }

}
