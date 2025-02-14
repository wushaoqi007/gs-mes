package com.greenstone.mes.machine.interfaces.rest;

import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockChangeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeResult;
import com.greenstone.mes.machine.application.service.MachineStockChangeService;
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
@RequestMapping("/stock/change")
public class MachineStockChangeApi extends BaseController {

    private final MachineStockChangeService stockChangeService;
    private final RemoteFileService fileService;

    @GetMapping("/list")
    public TableDataInfo stockChangeList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(stockChangeService.selectList(query));
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordQuery query) {
        startPage();
        return getDataTable(stockChangeService.listRecord(query));
    }

    @PostMapping("/record/export")
    public AjaxResult exportRecord(@RequestBody MachineRecordQuery query) {
        List<MachineStockChangeRecordExportR> list = stockChangeService.exportRecord(query);
        String fileName = "变更记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineStockChangeRecordExportR.class).sheet(fileName).doWrite(list);
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

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachinePartStockQuery query) {
        startPage();
        return getDataTable(stockChangeService.partChoose(query));
    }


    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineStockChangeResult result = stockChangeService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachinePartStockScanQuery query) {
        MachinePartStockR part = stockChangeService.scan(query);
        return AjaxResult.success(part);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineStockChangeAddCmd addCmd) {
        stockChangeService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineStockChangeAddCmd addCmd) {
        stockChangeService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        stockChangeService.remove(removeCmd);
        return AjaxResult.success();
    }
}
