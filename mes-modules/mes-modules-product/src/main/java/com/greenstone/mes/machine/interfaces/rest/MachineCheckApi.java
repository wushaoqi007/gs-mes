package com.greenstone.mes.machine.interfaces.rest;

import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckResultCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.application.service.MachineCheckService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/check")
public class MachineCheckApi extends BaseController {

    private final MachineCheckService checkService;
    private final RemoteFileService fileService;

    @GetMapping("/list")
    public TableDataInfo checkList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(checkService.selectList(query));
    }

    @GetMapping("/part/list")
    public TableDataInfo partList(MachineCheckPartListQuery query) {
        startPage();
        List<MachineCheckPartR> list = checkService.selectPartList(query);
        return getDataTable(list);
    }

    @GetMapping("/record")
    public TableDataInfo checkRecord(MachineCheckPartListQuery query) {
        startPage();
        query.setChecked("Y");
        List<MachineCheckRecord> list = checkService.listRecord(query);
        return getDataTable(list);
    }

    @GetMapping("/rework/record")
    public TableDataInfo reworkRecord(MachineRecordQuery query) {
        startPage();
        List<MachineCheckRecord> list = checkService.reworkRecord(query);
        return getDataTable(list);
    }

    @PostMapping("/rework/record/export")
    public AjaxResult exportReworkRecord(@RequestBody MachineRecordQuery query) {
        List<MachineReworkRecordExportR> list = checkService.exportRecord(query);
        String fileName = "返工记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineReworkRecordExportR.class).sheet(fileName).doWrite(list);
            // 将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", fileName + System.currentTimeMillis() + ".xlsx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return AjaxResult.success(upload.getData());
        } catch (IOException e) {
            log.error(fileName + "导出错误:" + e.getMessage());
            throw new RuntimeException(fileName + "导出错误");
        }
    }

    @GetMapping("/count")
    public AjaxResult checkCount(MachineCheckPartListQuery query) {
        query.setChecked("Y");
        List<MachineCheckCountR> list = checkService.checkCount(query);
        return AjaxResult.success(list);
    }

    @PostMapping("count/export")
    public void checkCountExport(HttpServletResponse response, @RequestBody @Validated MachineCheckPartListQuery query) {
        query.setChecked("Y");
        List<MachineCheckCountR> list = checkService.checkCount(query);
        ExcelUtil<MachineCheckCountR> util = new ExcelUtil<>(MachineCheckCountR.class);
        util.exportExcel(response, list, "检验数量导出");
    }

    @GetMapping("/result/part/scan")
    public AjaxResult partResultScan(@Validated MachineCheckPartScanQuery query) {
        MachineCheckPartR part = checkService.resultScan(query);
        return AjaxResult.success(part);
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineCheckResult result = checkService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineCheckAddCmd addCmd) {
        checkService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineCheckAddCmd addCmd) {
        checkService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        checkService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/result")
    public AjaxResult resultEntry(@RequestBody @Validated MachineCheckResultCmd resultCmd) {
        checkService.resultEntry(resultCmd);
        return AjaxResult.success("结果已录入");
    }

    @PostMapping("/print")
    public AjaxResult print(@RequestBody MachineExportQuery query) {
        log.info("开始打印检验单");
        SysFile file = checkService.print(query.getSerialNo());
        return AjaxResult.success(file);
    }
}
