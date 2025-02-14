package com.greenstone.mes.bom.request;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class MaterialImportReq {

    @Excel(name = "序号")
    @NotEmpty(message = "序号不能为空")
    private String index;

    @Excel(name = "机加工单号信息")
    @NotEmpty(message = "机加工单号信息不能为空")
    private String partOrderCode;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    /**
     * code+name+version，版本可以为空
     */
    @Excel(name = "零件号")
    @NotEmpty(message = "零件号不能为空")
    private String partCodeNameVersion;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Long number;

    @Excel(name = "说明")
    private String remark;

    @Excel(name = "质量（g）")
    private String weight;

    /**
     * 可能时 组件名称，也可能是 组件号+组件名称，组件号是两位数字
     */
    @Excel(name = "组件名称")
    @NotEmpty(message = "组件不能为空")
    private String componentName;

    @Excel(name = "购买区分")
    private String operation;

    @Excel(name = "打印日期", dateFormat = "yyyy/MM/dd")
    private Date printData;

    @Excel(name = "设计")
    @NotEmpty(message = "设计不能为空")
    private String designer;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "图纸张数")
    @NotNull(message = "图纸张数不能为空")
    private Integer paperNumber;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
