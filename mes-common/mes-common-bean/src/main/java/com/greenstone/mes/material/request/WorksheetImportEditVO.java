package com.greenstone.mes.material.request;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 机加工单导入批量修改
 */
@Data
@Valid
public class WorksheetImportEditVO {

    @Excel(name = "加工单号")
    @NotEmpty(message = "加工单号信息不能为空")
    private String worksheetCode;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Excel(name = "组件号")
    @NotEmpty(message = "组件号不能为空")
    private String componentCode;

    @Excel(name = "组件名称")
    @NotEmpty(message = "组件名称不能为空")
    private String componentName;

    @Excel(name = "零件号/版本")
    @NotEmpty(message = "零件号/版本不能为空")
    private String partCodeVersion;

    @Excel(name = "零件名称")
    @NotEmpty(message = "零件名称不能为空")
    private String partName;

    @Excel(name = "加工单位")
    private String provider;

    @Excel(name = "加工纳期", dateFormat = "yyyy/MM/dd")
    private Date processingTime;

    @Excel(name = "计划纳期", dateFormat = "yyyy/MM/dd")
    private Date planTime;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartCodeVersion {
        private String code;

        private String version;
    }

    public PartCodeVersion validAndGetPartCodeVersion() {
        String code;
        String version;
        // 拆分零件编码、版本。导出的有固定格式，/分割
        String[] codeVersion = partCodeVersion.split("/");
        if (codeVersion.length < 2) {
            throw new ServiceException(BizError.E25008, partCodeVersion);
        }
        code = codeVersion[0];
        version = codeVersion[1];
        return PartCodeVersion.builder().code(code).version(version).build();
    }

}
