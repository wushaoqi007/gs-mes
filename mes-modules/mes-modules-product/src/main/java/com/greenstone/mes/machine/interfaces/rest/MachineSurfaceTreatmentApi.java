package com.greenstone.mes.machine.interfaces.rest;

import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSurfaceTreatmentAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineSurfaceTreatmentPartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentResult;
import com.greenstone.mes.machine.application.service.MachineSurfaceTreatmentService;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/surface/treatment")
public class MachineSurfaceTreatmentApi extends BaseController {

    private final MachineSurfaceTreatmentService surfaceTreatmentService;
    private final RemoteFileService fileService;

    @GetMapping("/list")
    public TableDataInfo surfaceTreatmentList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(surfaceTreatmentService.selectList(query));
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineSurfaceTreatmentResult result = surfaceTreatmentService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordQuery query) {
        startPage();
        return getDataTable(surfaceTreatmentService.listRecord(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineSurfaceTreatmentPartScanQuery query) {
        MachineCheckPartStockR partStockR = surfaceTreatmentService.scan(query);
        return AjaxResult.success(partStockR);
    }

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachineCheckPartListQuery query) {
        startPage();
        return getDataTable(surfaceTreatmentService.partChoose(query));
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineSurfaceTreatmentAddCmd addCmd) {
        surfaceTreatmentService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineSurfaceTreatmentAddCmd addCmd) {
        surfaceTreatmentService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        surfaceTreatmentService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/record/export")
    public AjaxResult exportRecord(@RequestBody MachineRecordQuery query) {
        List<MachineSurfaceTreatmentRecordExportR> list = surfaceTreatmentService.exportRecord(query);
        String fileName = "表处记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineSurfaceTreatmentRecordExportR.class).sheet(fileName).doWrite(list);
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
}
