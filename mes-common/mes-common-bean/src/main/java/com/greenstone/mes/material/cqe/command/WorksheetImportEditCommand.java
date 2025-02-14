package com.greenstone.mes.material.cqe.command;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 机加工单导入批量修改
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class WorksheetImportEditCommand {

    @Valid
    @NotEmpty(message = "无法导入空数据")
    private List<WorksheetImportEditCommand.PartImportEditCommand> partImportEditCommands;

    @Data
    public static class PartImportEditCommand {
        @NotEmpty(message = "加工单号信息不能为空")
        private String worksheetCode;

        @NotEmpty(message = "项目代码不能为空")
        private String projectCode;

        @NotEmpty(message = "组件号不能为空")
        private String componentCode;

        @NotEmpty(message = "组件名称不能为空")
        private String componentName;

        @NotEmpty(message = "零件号不能为空")
        private String partCode;

        @NotEmpty(message = "版本不能为空")
        private String partVersion;

        @NotEmpty(message = "零件名称不能为空")
        private String partName;

        private String provider;

        private Date processingTime;

        private Date planTime;
    }

    public void validate() {
        this.validateWorksheetCode();
        this.validateProjectCode();
    }

    private void validateWorksheetCode() {
        String workOrderCode = null;
        for (WorksheetImportEditCommand.PartImportEditCommand importCommand : this.partImportEditCommands) {
            // 同一次上传的加工单号必须一致
            if (workOrderCode == null) {
                workOrderCode = importCommand.getWorksheetCode();
            } else if (!workOrderCode.equals(importCommand.getWorksheetCode())) {
                throw new ServiceException(BizError.E25003, StrUtil.format("'{}'和'{}'", workOrderCode, importCommand.getWorksheetCode()));
            }
        }
        // 加工单号的规则：客户代码+7位数字+8位日期+3位字母+2位序号
        if (!workOrderCode.matches("[A-Z]+[0-9]{15}[A-Z]{3}[0-9]{2}") && !workOrderCode.matches("[A-Z]+[0-9]{4}[A-Z]{2}[0-9]{3}[0-9]{8}[A-Z]{3}[0-9]{2}")) {
            throw new ServiceException(BizError.E25002, StrUtil.format("客户代码+（7位数字）或（4位数字+2位字母+3位数字）+8位日期+3位字母+2位序号。", workOrderCode));
        }
    }


    private void validateProjectCode() {
        String projectCode = null;
        for (WorksheetImportEditCommand.PartImportEditCommand importCommand : this.partImportEditCommands) {
            if (projectCode == null) {
                projectCode = importCommand.getProjectCode();
            } else if (!projectCode.equals(importCommand.getProjectCode())) {
                throw new ServiceException(BizError.E25004, StrUtil.format("一次导入不能包含多个项目代码：'{}'和'{}'", projectCode, importCommand.getProjectCode()));
            }
        }
    }

}
