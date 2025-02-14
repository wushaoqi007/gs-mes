package com.greenstone.mes.material.request;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 项目导入
 */
@Data
@Valid
public class ProjectImportVO {

    @Excel(name = "产品代码")
    @NotEmpty(message = "产品代码不能为空")
    private String projectCode;

    @Excel(name = "客户名称")
    @NotEmpty(message = "客户名称不能为空")
    private String customerName;

    @Excel(name = "客户简称")
    @NotEmpty(message = "客户简称不能为空")
    private String customerShortName;

    @Excel(name = "立项日期")
    private String projectInitiationTime;

    @Excel(name = "GS组织")
    private String gsOrganization;

    @Excel(name = "生产类型")
    private String productionType;

    @Excel(name = "产品名称")
    @NotEmpty(message = "产品名称不能为空")
    private String projectName;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Integer number;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "设计纳期")
    private String designDeadline;

    @Excel(name = "客户纳期")
    @NotNull(message = "客户纳期不能为空")
    private String customerDeadline;

    @Excel(name = "订单号")
    private String orderCode;

    @Excel(name = "订单接收日")
    private String orderReceiveTime;

    @Excel(name = "客户担当")
    private String customerDirector;

    @Excel(name = "设计担当")
    private String designerDirector;

    @Excel(name = "电气担当")
    private String electricalDirector;

    @Excel(name = "是否需软件部参与", readConverterExp = "1=是,0=否")
    private String softwareJoin;

    @Excel(name = "软件担当")
    private String softwareDirector;

    @Excel(name = "业务担当")
    private String businessDirector;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "一次报价")
    private String firstQuotation;

    @Excel(name = "最终报价单")
    private String lastQuotation;

    @Excel(name = "同一订单")
    private String sameOrder;


}
