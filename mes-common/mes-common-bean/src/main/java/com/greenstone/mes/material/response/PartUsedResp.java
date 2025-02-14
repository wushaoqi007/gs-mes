
package com.greenstone.mes.material.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.*;

import java.util.Date;

/**
 * 已领取零件
 *
 * @author wushaoqi
 * @date 2022-11-09-13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartUsedResp {

    @Excel(name = "加工单号")
    private String worksheetCode;

    @Excel(name = "项目代码")
    private String projectCode;

    @Excel(name = "组件号")
    private String componentCode;

    @Excel(name = "组件名称")
    private String componentName;

    @Excel(name = "零件号")
    private String partCode;

    @Excel(name = "零件版本")
    private String partVersion;

    @Excel(name = "零件名称")
    private String partName;

    @Excel(name = "零件数量")
    private Integer total;

    @Excel(name = "已领用数量")
    private Integer usedNum;

    @Excel(name = "加工商")
    private String provider;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加工纳期", dateFormat = "yyyy-MM-dd")
    private Date processingTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划纳期", dateFormat = "yyyy-MM-dd")
    private Date planTime;

    @Excel(name = "设计")
    private String designer;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "重量（g）")
    private String weight;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "入库时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstInTime;
}
