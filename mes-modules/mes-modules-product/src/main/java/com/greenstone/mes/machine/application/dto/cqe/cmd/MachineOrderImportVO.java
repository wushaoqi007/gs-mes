package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单导入
 */
@Data
public class MachineOrderImportVO {

    @Excel(name = "询价单号")
    @NotEmpty(message = "询价单号不能为空")
    private String inquiryPriceSerialNo;

    @Excel(name = "发图日期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "发图日期不能为空")
    private Date orderTime;

    @Excel(name = "申请单号")
    @NotEmpty(message = "申请单号不能为空")
    private String requirementSerialNo;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Excel(name = "组件号")
    private String componentCode;

    @Excel(name = "机种名称")
    @NotEmpty(message = "机种名称不能为空")
    private String hierarchy;

    @Excel(name = "零件号/版本")
    @NotEmpty(message = "零件号/版本不能为空")
    private String partCodeAndVersion;

    @Excel(name = "零件名称")
    @NotEmpty(message = "零件名称不能为空")
    private String partName;

    @Excel(name = "数量")
    @NotNull(message = "数量不为空")
    private Integer processNumber;

    @Excel(name = "设计")
    @NotEmpty(message = "设计不能为空")
    private String designer;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "质量g")
    private String weight;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "加工单位")
    @NotEmpty(message = "加工单位不能为空")
    private String provider;

    @Excel(name = "加工纳期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "加工纳期不为空")
    private Date processDeadline;

    @Excel(name = "计划纳期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "计划纳期不为空")
    private Date planDeadline;

    @Excel(name = "含税单价")
    private BigDecimal unitPrice;

    @Excel(name = "含税总价")
    private BigDecimal totalPrice;

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
