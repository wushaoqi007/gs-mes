package com.greenstone.mes.product.application.dto.cmd;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
public class ProductPlanImportVO {

    @Excel(name = "计划状态")
    private String planStatus;

    @Excel(name = "计划编号")
    private String serialNo;

    @Excel(name = "项目代码")
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @Excel(name = "类型")
    @NotEmpty(message = "类型不能为空")
    private String levelName;

    @Excel(name = "名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Integer number;

    @Excel(name = "计划开始时间", dateFormat = "yyyy-MM-dd")
    @NotNull(message = "计划开始时间不能为空")
    private Date planStartTime;

    @Excel(name = "计划结束时间", dateFormat = "yyyy-MM-dd")
    @NotNull(message = "计划结束时间不能为空")
    private Date planEndTime;

    @Excel(name = "实际开始时间", dateFormat = "yyyy-MM-dd")
    private Date actualStartTime;

    @Excel(name = "实际结束时间", dateFormat = "yyyy-MM-dd")
    private Date actualEndTime;

    @Excel(name = "完成率")
    private Double completionRate;

    @Excel(name = "变更类型")
    private String planChangeType;

    @Excel(name = "变更原因")
    private String reason;

    @Excel(name = "责任部门")
    private String dept;

    public void trim() {
        List<String> trims = List.of(" ", "-", "_", "/");
        if (serialNo != null) {
            serialNo = StrUtil.trim(serialNo, 0, character -> trims.contains(String.valueOf(character)));
            serialNo = serialNo.replaceAll("\r", "");
            serialNo = serialNo.replaceAll("\n", "");
        }
        projectCode = StrUtil.trim(projectCode, 0, character -> trims.contains(String.valueOf(character)));
        projectCode = projectCode.replaceAll("\r", "");
        projectCode = projectCode.replaceAll("\n", "");

        name = StrUtil.trim(name, 0, character -> trims.contains(String.valueOf(character)));
        name = name.replaceAll("\r", "");
        name = name.replaceAll("\n", "");

        levelName = StrUtil.trim(levelName, 0, character -> trims.contains(String.valueOf(character)));
        levelName = levelName.replaceAll("\r", "");
        levelName = levelName.replaceAll("\n", "");

    }

}
