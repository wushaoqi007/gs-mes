package com.greenstone.mes.material.cqe.command;

import cn.hutool.core.util.StrUtil;
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
public class WorksheetUpdateCommand {

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

        @NotEmpty(message = "加工单位不能为空")
        private String provider;

        @NotNull(message = "加工纳期不能为空")
        private Date processingTime;

        @NotNull(message = "计划纳期不能为空")
        private Date planTime;


        public void trim() {
            List<String> trims = List.of(" ", "-", "_");
            name = StrUtil.trim(name, 0, character -> trims.contains(String.valueOf(character)));
            name = name.replaceAll("\r", "");
            name = name.replaceAll("\n", "");
        }


    }

}
