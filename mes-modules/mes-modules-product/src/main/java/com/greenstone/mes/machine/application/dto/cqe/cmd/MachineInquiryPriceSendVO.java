package com.greenstone.mes.machine.application.dto.cqe.cmd;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineInquiryPriceSendVO {

    @Excel(name = "询价单号")
    @NotEmpty(message = "询价单号不能为空")
    private String serialNo;

    @Excel(name = "订单号", width = 30)
    private String requirementSerialNo;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Excel(name = "机种名称")
    private String hierarchy;

    @Excel(name = "零件号/版本")
    @NotEmpty(message = "零件号/版本不能为空")
    private String partCodeAndVersion;

    @Excel(name = "零件名称")
    @NotEmpty(message = "零件名称不能为空")
    private String partName;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Long partNumber;

    @Excel(name = "设计")
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
    private String provider;

    @NotEmpty(message = "加工纳期不能为空")
    @Excel(name = "加工纳期", dateFormat = "yyyy/MM/dd")
    private String processDeadline;

}
