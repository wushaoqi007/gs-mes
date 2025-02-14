package com.greenstone.mes.asset.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetExportResult {

    @Excel(name = "资产编号")
    private String barCode;

    @Excel(name = "名称")
    private String name;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "序列号")
    private String sn;

    @Excel(name = "分类编码")
    private String typeCode;

    @Excel(name = "分类名称")
    private String typeName;

    @Excel(name = "完整分类")
    private String typeHierarchy;

    @Excel(name = "购买日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchasedDate;

    @Excel(name = "状态")
    private AssetState state;

    @Excel(name = "数量单位")
    private String unit;

    @Excel(name = "领用人")
    private String receivedBy;

    @Excel(name = "部门")
    private String deptName;

    @Excel(name = "工号")
    private String employeeNo;

    @Excel(name = "领用时间", dateFormat = "yyyy/MM/dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime receivedTime;

}
