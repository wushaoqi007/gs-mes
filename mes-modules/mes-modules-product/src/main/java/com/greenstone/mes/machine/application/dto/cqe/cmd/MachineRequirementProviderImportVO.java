package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 加工商信息导入
 */
@Data
public class MachineRequirementProviderImportVO {


    @Excel(name = "申请单号")
    @NotEmpty(message = "申请单号不能为空")
    private String requirementSerialNo;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Excel(name = "组件号")
    private String componentCode;

    @Excel(name = "机种名称")
    private String hierarchy;

    @Excel(name = "零件号/版本")
    @NotEmpty(message = "零件号/版本不能为空")
    private String partCodeAndVersion;

    @Excel(name = "零件名称")
    @NotEmpty(message = "零件名称不能为空")
    private String partName;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "加工单位")
    @NotEmpty(message = "加工单位不能为空")
    private String provider;

    @Excel(name = "加工纳期", dateFormat = "yyyy-MM-dd")
    @NotNull(message = "加工纳期不为空")
    private Date processDeadline;

    @Excel(name = "计划纳期", dateFormat = "yyyy-MM-dd")
    @NotNull(message = "计划纳期不为空")
    private Date planDeadline;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String validAndGetPartCode() {
        String[] codeVersion = partCodeAndVersion.split("/");
        if (codeVersion.length < 2) {
            throw new ServiceException(StrUtil.format("零件号/版本不正确: {}", partCodeAndVersion));
        }
        return codeVersion[0];
    }

    public String validAndGetPartVersion() {
        String[] codeVersion = partCodeAndVersion.split("/");
        if (codeVersion.length < 2) {
            throw new ServiceException(StrUtil.format("零件号/版本不正确: {}", partCodeAndVersion));
        }
        return codeVersion[1];
    }

}
