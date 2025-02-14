package com.greenstone.mes.material.response;

import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.export.DictMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderExportResp {

    @Excel(name = "加工单号")
    private String worksheetCode;

    @Excel(name = "项目代码")
    private String projectCode;

    @Excel(name = "组件号")
    private String componentCode;

    @Excel(name = "组件名称")
    private String componentName;

    @Excel(name = "零件号/版本")
    private String codeVersion;

    @Excel(name = "零件名称")
    private String name;

    @Excel(name = "数量")
    private Long materialNumber;

    @Excel(name = "图纸数量")
    private Integer paperNumber;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "质量g")
    private String weight;

    @Excel(name = "到货数量")
    private Integer getNumber;

    @Excel(name = "进度")
    @DictMapping(dictType = "purchase_part_status")
    private String status;

    @Excel(name = "是否采购")
    private String isPurchase;

    @Excel(name = "是否加急")
    private String isFast;

    @Excel(name = "加工单位")
    private String provider;

    @Excel(name = "加工纳期", dateFormat = "yyyy-MM-dd")
    private Date processingTime;

    @Excel(name = "计划纳期", dateFormat = "yyyy-MM-dd")
    private Date planTime;

    @Excel(name = "类型")
    private String type;
}
