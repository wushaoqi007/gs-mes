package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineMaterialUseFinishCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialUseResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineMaterialUseService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/material/use")
public class MachineMaterialUseApi extends BaseController {

    private final MachineMaterialUseService materialUseService;
    private final MachineHelper machineHelper;

    @GetMapping("/list")
    public TableDataInfo materialUseList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(materialUseService.selectList(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineOrderPartScanQuery query) {
        MachineOrderPartR part = materialUseService.scan(query);
        return AjaxResult.success(part);
    }

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachineOrderPartListQuery query) {
        startPage();
        return getDataTable(materialUseService.partChoose(query));
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineMaterialUseResult result = materialUseService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineMaterialUseAddCmd addCmd) {
        materialUseService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineMaterialUseAddCmd addCmd) {
        materialUseService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        materialUseService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PutMapping("/finish")
    public AjaxResult finish(@Validated @RequestBody MachineMaterialUseFinishCmd finishCmd) {
        materialUseService.finish(finishCmd);
        return AjaxResult.success("领料完成");
    }

    @PostMapping("/export")
    public AjaxResult export(@RequestBody MachineExportQuery query) {
        log.info("开始导出领料单word");
        MachineMaterialUseResult result = materialUseService.detail(query.getSerialNo());
        SysFile file = machineHelper.materialUseGenWord(result);
        return AjaxResult.success(file);
    }

}
