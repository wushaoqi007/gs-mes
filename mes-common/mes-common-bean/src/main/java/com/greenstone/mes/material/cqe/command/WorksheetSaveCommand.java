package com.greenstone.mes.material.cqe.command;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.enums.PartBuyReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/1 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksheetSaveCommand {

    @NotEmpty(message = "加工单编码不能为空")
    private String code;

    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @NotNull(message = "公司不能为空")
    private Integer company;

    @Valid
    @NotEmpty
    private List<ProcessComponent> components;

    public void trim() {
        for (ProcessComponent component : components) {
            component.trim();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessComponent {
        @NotEmpty(message = "组件号不能为空")
        private String code;

        @NotEmpty(message = "组件名称不能为空")
        private String name;

        @Valid
        @NotEmpty
        List<ProcessPart> parts;

        public void trim() {
            for (ProcessPart part : parts) {
                part.trim();
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessPart {

        @NotEmpty(message = "零件号不能为空")
        private String code;

        @NotEmpty(message = "零件版本不能为空")
        private String version;

        @NotEmpty(message = "零件名称不能为空")
        private String name;

        @NotNull(message = "零件数量不能为空")
        private Integer number;

        @NotNull(message = "图纸数量不能为空")
        private Integer paperNumber;

        @NotNull(message = "购买原因不能为空")
        private PartBuyReason reason;

        @NotEmpty(message = "设计不能为空")
        private String designer;

        @NotNull(message = "打印日期不能为空")
        private Date printDate;

        private String rawMaterial;

        private String surfaceTreatment;

        private String weight;

        public void trim() {
            List<String> trims = List.of(" ", "-", "_");
            name = StrUtil.trim(name, 0, character -> trims.contains(String.valueOf(character)));
            name = name.replaceAll("\r", "");
            name = name.replaceAll("\n", "");
            rawMaterial = StrUtil.trim(rawMaterial, 0, character -> trims.contains(String.valueOf(character)));
            rawMaterial = rawMaterial.replaceAll("\r", "");
            rawMaterial = rawMaterial.replaceAll("\n", "");
            surfaceTreatment = StrUtil.trim(surfaceTreatment, 0, character -> trims.contains(String.valueOf(character)));
            surfaceTreatment = surfaceTreatment.replaceAll("\r", "");
            surfaceTreatment = surfaceTreatment.replaceAll("\n", "");
        }


    }

}
