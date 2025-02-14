package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:48
 */
@Data
public class MachineRequirementExtraEditCmd {
    private String id;
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    private String projectCode;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        private String id;
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

        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "加工纳期不为空")
        private LocalDate processDeadline;
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "计划纳期不为空")
        private LocalDate planDeadline;
        private String partType;
        private Boolean urgent;
        @NotBlank(message = "加工单位不为空")
        private String provider;

        public void trim() {
            List<String> trims = List.of(" ", "-", "_");
            provider = StrUtil.trim(provider, 0, character -> trims.contains(String.valueOf(character)));
            provider = provider.replaceAll("\r", "");
            provider = provider.replaceAll("\n", "");
        }
    }


    public void trim() {
        for (Part part : this.parts) {
            part.trim();
        }
    }
}
