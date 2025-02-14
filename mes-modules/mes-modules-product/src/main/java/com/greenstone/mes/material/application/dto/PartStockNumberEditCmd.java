package com.greenstone.mes.material.application.dto;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-08-16-9:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartStockNumberEditCmd {

    @Valid
    private List<Material> materialList;

    @NotEmpty(message = "操作人不能为空")
    private String sponsor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Material {
        @NotNull(message = "仓库不能为空")
        private Long warehouseId;

        @NotEmpty(message = "加工单号不能为空")
        private String worksheetCode;

        @NotEmpty(message = "项目号不能为空")
        private String projectCode;

        @NotEmpty(message = "组件号不能为空")
        private String componentCode;

        private String partName;

        @NotEmpty(message = "零件号不能为空")
        private String partCode;

        @NotEmpty(message = "版本不能为空")
        private String partVersion;

        @NotNull(message = "数量不能为空")
        private Long number;

        private String remark;
        private BaseMaterial material;
        private BaseWarehouse warehouse;
    }

}
