package com.greenstone.mes.material.cqe.command;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.enums.PartBuyReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class WorksheetImportCommand {

    @NotNull(message = "公司不能为空")
    private Integer company;

    @Valid
    @NotEmpty(message = "无法导入空数据")
    private List<PartImportCommand> partImportCommands;

    @Data
    public static class PartImportCommand {

        @NotEmpty(message = "机加工单号不能为空")
        private String processOrderCode;

        @NotEmpty(message = "项目代码不能为空")
        private String projectCode;

        @NotEmpty(message = "组件编码不能为空")
        private String componentCode;

        @NotEmpty(message = "组件名称不能为空")
        private String componentName;

        @NotEmpty(message = "零件号不能为空")
        private String partCode;

        @NotEmpty(message = "零件名称不能为空")
        private String partName;

        @NotEmpty(message = "零件版本不能为空")
        private String partVersion;

        @NotNull(message = "零件数量不能为空")
        private Integer partNumber;

        @NotNull(message = "图纸张数不能为空")
        private Integer paperNumber;

        @NotNull(message = "购买原因不能为空")
        private PartBuyReason reason;

        @NotEmpty(message = "设计不能为空")
        private String designer;

        @NotNull(message = "打印日期不能为空")
        private Date printDate;

        private String unit;

        private String rawMaterial;

        private String surfaceTreatment;

        private String weight;

        private String remark;

    }

    public void validate() {
        this.validateWorksheetCode();
        this.validateProjectCode();
    }

    private void validateWorksheetCode() {
        String processOrderCode = null;
        for (PartImportCommand importCommand : this.partImportCommands) {
            // 同一次上传的加工单号必须一致
            if (processOrderCode == null) {
                processOrderCode = importCommand.getProcessOrderCode();
            } else if (!processOrderCode.equals(importCommand.getProcessOrderCode())) {
                throw new ServiceException(BizError.E25003, StrUtil.format("'{}'和'{}'", processOrderCode, importCommand.getProcessOrderCode()));
            }
        }
        // 加工单号的规则：客户代码+7位数字+8位日期+3位字母+2位序号
        if (!processOrderCode.matches("[A-Z]+[0-9]{15}[A-Z]{3}[0-9]+") && !processOrderCode.matches("[A-Z]+[0-9]{4}[A-Z]{2}[0-9]{3}[0-9]{8}[A-Z]{3}[0-9]+")) {
            throw new ServiceException(BizError.E25002, StrUtil.format("客户代码+（7位数字）或（4位数字+2位字母+3位数字）+8位日期+3位字母+2位序号。", processOrderCode));
        }
    }


    private void validateProjectCode() {
        String projectCode = null;
        for (PartImportCommand importCommand : this.partImportCommands) {
            if (projectCode == null) {
                projectCode = importCommand.getProjectCode();
            } else if (!projectCode.equals(importCommand.getProjectCode())) {
                throw new ServiceException(StrUtil.format("一次导入不能包含多个项目代码：'{}'和'{}'", projectCode, importCommand.getProjectCode()));
            }
        }
    }

}
