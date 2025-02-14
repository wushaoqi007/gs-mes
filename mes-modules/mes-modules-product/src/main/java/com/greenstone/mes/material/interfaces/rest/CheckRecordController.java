package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.material.domain.service.CheckRecordService;
import com.greenstone.mes.material.dto.CheckRecordExportCommand;
import com.greenstone.mes.material.dto.CheckRecordListQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 检验记录控制类
 *
 * @author gu_renkai
 * @date 2022-08-03
 */
@Slf4j
@RestController
@RequestMapping("/check/record")
public class CheckRecordController extends BaseController {

    private final CheckRecordService checkRecordService;

    public CheckRecordController(CheckRecordService checkRecordService) {
        this.checkRecordService = checkRecordService;
    }

    @GetMapping("/list")
    public TableDataInfo list(CheckRecordListQuery query) {
        startPage();
        return getDataTable(checkRecordService.list(query));
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody CheckRecordExportCommand exportCommand) {
        XSSFWorkbook excel = checkRecordService.export(exportCommand);
        new ExcelUtil<>().writeToHttp(response, excel, "检验结果");
    }

}