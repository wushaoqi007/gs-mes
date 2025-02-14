package com.greenstone.mes.machine.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderContractExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderProgressQuery;
import com.greenstone.mes.machine.application.dto.result.MachineOrderExportR;
import com.greenstone.mes.machine.application.dto.result.MachineOrderProgressExportResult;
import com.greenstone.mes.machine.application.service.MachineOrderService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tables/100000018")
public class MachineOrdersApi extends BaseController {

    private final MachineOrderService orderService;

    @ApiLog
    @GetMapping("/progress/list")
    public TableDataInfo orderProgressList(@Validated MachineOrderProgressQuery query) {
        startPage();
        return getDataTable(orderService.selectOrderProgressList(query));
    }

    @ApiLog
    @PostMapping("/contract/print")
    public SysFile contractPrint(@RequestBody @Validated MachineOrderContractExportQuery query) {
        return orderService.contractPrint(query);
    }

    @PostMapping("/export")
    public void exportData(HttpServletResponse response, @RequestBody @Validated MachineOrderExportQuery query) {
        List<MachineOrderExportR> list = orderService.selectExportDataList(query);
        ExcelUtil<MachineOrderExportR> util = new ExcelUtil<>(MachineOrderExportR.class);
        util.exportExcel(response, list, StrUtil.format("{}订单", query.getMonth()));
    }

    @PostMapping("/progress/export")
    public void exportProgress(HttpServletResponse response, @RequestBody @Validated MachineOrderProgressQuery query) {
        List<MachineOrderProgressExportResult> list = orderService.selectOrderProgressExportList(query);
        ExcelUtil<MachineOrderProgressExportResult> util = new ExcelUtil<>(MachineOrderProgressExportResult.class);
        util.exportExcel(response, list, "订单查询导出");
    }


}
