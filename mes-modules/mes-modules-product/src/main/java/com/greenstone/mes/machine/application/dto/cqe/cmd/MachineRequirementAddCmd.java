package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineRequirementAddCmd {
    private String id;
    @NotEmpty(message = "需求单号不为空")
    private String serialNo;
    @NotEmpty(message = "项目代码不为空")
    private String projectCode;
    private Long applyById;
    private LocalDateTime applyTime;
    private String remark;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String id;
        @NotEmpty(message = "需求单号不为空")
        private String serialNo;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
        private Long materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "单套数量不为空")
        private Long perSet;
        @NotNull(message = "套数不为空")
        private Integer setsNumber;
        @NotNull(message = "加工数量不为空")
        private Long processNumber;
        private Integer paperNumber;
        private String surfaceTreatment;
        private String rawMaterial;
        private String weight;
        private Date printDate;
        private String hierarchy;
        private String designer;
        private String remark;

        public void trim() {
            List<String> trims = List.of(" ", "-", "_", "/");
            partName = StrUtil.trim(partName, 0, character -> trims.contains(String.valueOf(character)));
            partName = partName.replaceAll("\r", "");
            partName = partName.replaceAll("\n", "");
            if (StrUtil.isNotEmpty(rawMaterial)) {
                rawMaterial = StrUtil.trim(rawMaterial, 0, character -> trims.contains(String.valueOf(character)));
                rawMaterial = rawMaterial.replaceAll("\r", "");
                rawMaterial = rawMaterial.replaceAll("\n", "");
            }
            if (StrUtil.isNotEmpty(surfaceTreatment)) {
                surfaceTreatment = StrUtil.trim(surfaceTreatment, 0, character -> trims.contains(String.valueOf(character)));
                surfaceTreatment = surfaceTreatment.replaceAll("\r", "");
                surfaceTreatment = surfaceTreatment.replaceAll("\n", "");
            }
            if (StrUtil.isNotEmpty(hierarchy)) {
                hierarchy = StrUtil.trim(hierarchy, 0, character -> trims.contains(String.valueOf(character)));
                hierarchy = hierarchy.replaceAll("\r", "");
                hierarchy = hierarchy.replaceAll("\n", "");
            }
        }
    }


    public void trim() {
        for (Part part : this.parts) {
            part.trim();
        }
    }

}
