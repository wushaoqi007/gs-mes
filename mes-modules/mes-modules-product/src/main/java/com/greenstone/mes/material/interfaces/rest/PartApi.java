package com.greenstone.mes.material.interfaces.rest;

import com.alibaba.excel.ExcelWriter;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.material.application.dto.PartCheckCmd;
import com.greenstone.mes.material.application.dto.PartProgressQuery;
import com.greenstone.mes.material.application.dto.result.PartProgressR;
import com.greenstone.mes.material.application.dto.result.ProjectProgressR;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import com.greenstone.mes.material.application.service.WorksheetManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-26-15:16
 */
@Slf4j
@RestController
@RequestMapping("/part")
public class PartApi extends BaseController {

    private final PartStageStatusManager partStageStatusManager;
    private final WorksheetManager worksheetManager;

    public PartApi(PartStageStatusManager partStageStatusManager, WorksheetManager worksheetManager) {
        this.partStageStatusManager = partStageStatusManager;
        this.worksheetManager = worksheetManager;
    }

    @GetMapping("/project/progress")
    public AjaxResult selectProjectProgress(PartProgressQuery partProgressQuery) {
        ProjectProgressR list = partStageStatusManager.selectProjectProgress(partProgressQuery);
        return AjaxResult.success(list);
    }

    @GetMapping("/progress")
    public AjaxResult selectPartProgress(PartProgressQuery partProgressQuery) {
        List<PartProgressR> list = partStageStatusManager.selectPartProgress(partProgressQuery);
        return AjaxResult.success(list);
    }

    @PostMapping("/export/progress")
    public void exportAbsentStat(HttpServletResponse response, @RequestBody PartProgressQuery partProgressQuery) {
        ExcelWriter excelWriter = partStageStatusManager.makeProjectPartProgressExcel(response, partProgressQuery);
        ExcelUtil<PartProgressR> excelUtil = new ExcelUtil<>();
        excelUtil.writeEasyExcelToHttp(response, excelWriter, "项目零件进度查询");
    }

    @PostMapping("/check")
    public AjaxResult checkPart(@RequestBody @Validated PartCheckCmd partCheckCmd) {
        return AjaxResult.success(worksheetManager.checkPart(partCheckCmd));
    }

}
