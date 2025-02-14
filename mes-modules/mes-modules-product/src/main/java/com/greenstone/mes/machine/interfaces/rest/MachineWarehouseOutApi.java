package com.greenstone.mes.machine.interfaces.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutResult;
import com.greenstone.mes.machine.application.service.MachineWarehouseOutService;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
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
@RequestMapping("/warehouse/out")
public class MachineWarehouseOutApi extends BaseController {

    private final MachineWarehouseOutService warehouseOutService;
    private final RemoteFileService fileService;

    @GetMapping("/list")
    public TableDataInfo warehouseOutList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(warehouseOutService.selectList(query));
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordQuery query) {
        startPage();
        return getDataTable(warehouseOutService.listRecord(query));
    }

    @PostMapping("/record/export")
    public AjaxResult exportRecord(@RequestBody MachineRecordQuery query) {
        List<MachineWarehouseOutRecordExportR> list = warehouseOutService.exportRecord(query);
        String fileName = "出库记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineWarehouseOutRecordExportR.class).sheet(fileName).doWrite(list);
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

    @GetMapping("/stock/all")
    public AjaxResult stockAll(@Validated MachineStockAllQuery query) {
        List<MachinePartStockR> stockRList = warehouseOutService.stockAll(query);
        return AjaxResult.success(stockRList);
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineWarehouseOutResult result = warehouseOutService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineWarehouseOutAddCmd addCmd) {
        warehouseOutService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineWarehouseOutAddCmd addCmd) {
        warehouseOutService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        warehouseOutService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody @Validated MachineSignCmd signCmd) {
        String spNo = warehouseOutService.sign(signCmd);
        return AjaxResult.success(StrUtil.format("请前往企业微信查看审批并完成签字，单号：{}", spNo));
    }

    @PostMapping("/sign/finish")
    public AjaxResult signFinish(@RequestBody @Validated MachineSignFinishCmd finishCmd) {
        log.info("出库签字完成：{}", finishCmd);
        warehouseOutService.signFinish(finishCmd);
        return AjaxResult.success("已签字");
    }

    @PostMapping("/print")
    public AjaxResult print(@RequestBody MachineExportQuery query) {
        log.info("开始打印出库单");
        SysFile file = warehouseOutService.print(query.getSerialNo());
        return AjaxResult.success(file);
    }
}
