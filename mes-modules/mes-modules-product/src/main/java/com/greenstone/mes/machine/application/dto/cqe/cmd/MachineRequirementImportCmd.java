package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
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
 * @author wushaoqi
 * @date 2023-11-23-15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineRequirementImportCmd {
    @Valid
    @NotEmpty(message = "无法导入空数据")
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @NotEmpty(message = "需求单号不为空")
        private String serialNo;
        @NotEmpty(message = "项目代码不为空")
        private String projectCode;
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
        private Long originalNumber;
        private Long processNumber;
        @NotNull(message = "图纸数量不为空")
        private Integer paperNumber;
        private String surfaceTreatment;
        private String rawMaterial;
        private String weight;
        @NotNull(message = "打印日期不为空")
        private Date printDate;
        private String hierarchy;
        @NotEmpty(message = "设计不为空")
        private String designer;
        private String remark;
    }

    public void validate() {
        this.validateMachineRequirement();
    }

    private void validateMachineRequirement() {
        String serialNo = null;
        String projectCode = null;
        for (Part part : this.parts) {
            // 同一次上传的加工单号必须一致
            if (serialNo == null) {
                serialNo = part.getSerialNo();
            } else if (!serialNo.equals(part.getSerialNo())) {
                throw new ServiceException(MachineError.E200003, StrUtil.format("'{}'和'{}'", serialNo, part.getSerialNo()));
            }
            // 同一次上传的项目号必须一致
            if (projectCode == null) {
                projectCode = part.getProjectCode();
            } else if (!projectCode.equals(part.getProjectCode())) {
                throw new ServiceException(MachineError.E200004, StrUtil.format("'{}'和'{}'", projectCode, part.getProjectCode()));
            }
        }
        // 加工单号的规则：客户代码+7位数字+8位日期+3位字母+2位序号
        if (!serialNo.matches("[A-Z]+[0-9]{15}[A-Z]{3}[0-9]+") && !serialNo.matches("[A-Z]+[0-9]{4}[A-Z]{2}[0-9]{3}[0-9]{8}[A-Z]{3}[0-9]+")) {
            throw new ServiceException(MachineError.E200002, StrUtil.format("客户代码+（7位数字）或（4位数字+2位字母+3位数字）+8位日期+3位字母+2位序号。", serialNo));
        }
    }

}
