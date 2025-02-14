package com.greenstone.mes.machine.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckTakeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.service.MachineCheckTakeService;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
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
@RequestMapping("/check/take")
public class MachineCheckTakeApi extends BaseController {

    private final MachineCheckTakeService checkTakeService;

    @GetMapping("/list")
    public TableDataInfo checkList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(checkTakeService.selectList(query));
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordFuzzyQuery query) {
        startPage();
        return getDataTable(checkTakeService.listRecord(query));
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineCheckTakeResult result = checkTakeService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineOrderPartScanQuery query) {
        MachineOrderPartR part = checkTakeService.scan(query);
        return AjaxResult.success(part);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineCheckTakeAddCmd addCmd) {
        checkTakeService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineCheckTakeAddCmd addCmd) {
        checkTakeService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody @Validated MachineSignCmd signCmd) {
        String spNo = checkTakeService.sign(signCmd);
        return AjaxResult.success(StrUtil.format("请前往企业微信查看审批并完成签字，单号：{}", spNo));
    }

    @PostMapping("/sign/finish")
    public AjaxResult signFinish(@RequestBody @Validated MachineSignFinishCmd finishCmd) {
        log.info("质检取件签字完成：{}", finishCmd);
        checkTakeService.signFinish(finishCmd);
        return AjaxResult.success("已签字");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        checkTakeService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/print")
    public AjaxResult print(@RequestBody MachineExportQuery query) {
        log.info("开始打印质检取件单");
        SysFile file = checkTakeService.print(query.getSerialNo());
        return AjaxResult.success(file);
    }

}
