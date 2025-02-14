package com.greenstone.mes.machine.interfaces.rest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.assemble.MachineReceiveAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportVO;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveExportR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineReceiveResult;
import com.greenstone.mes.machine.application.service.MachineReceiveService;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/receive")
public class MachineReceiveApi extends BaseController {

    private final MachineReceiveService receiveService;
    private final MachineReceiveAssemble receiveAssemble;
    private final RemoteFileService fileService;

    @GetMapping("/list")
    public TableDataInfo receiveList(MachineFuzzyQuery query) {
        startPage();
        List<String> fields = new ArrayList<>();
        fields.add("serialNo");
        fields.add("provider");
        fields.add("remark");
        query.setFields(fields);
        return getDataTable(receiveService.selectList(query));
    }

    @GetMapping("/record")
    public TableDataInfo listRecord(MachineRecordQuery query) {
        startPage();
        return getDataTable(receiveService.listRecord(query));
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachineReceivePartScanQuery query) {
        MachineOrderPartR part = receiveService.scan(query);
        return AjaxResult.success(part);
    }

    @GetMapping("/part/choose")
    public TableDataInfo partChoose(MachineOrderPartListQuery query) {
        startPage();
        return getDataTable(receiveService.partChoose(query));
    }

    @GetMapping(value = "/{serialNo}")
    public AjaxResult detail(@PathVariable("serialNo") String serialNo) {
        MachineReceiveResult result = receiveService.detail(serialNo);
        return AjaxResult.success(result);
    }

    @PostMapping("/draft")
    public AjaxResult draft(@RequestBody @Validated MachineReceiveAddCmd addCmd) {
        receiveService.saveDraft(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PostMapping("/commit")
    public AjaxResult commit(@RequestBody @Validated MachineReceiveAddCmd addCmd) {
        receiveService.saveCommit(addCmd);
        return AjaxResult.success("提交成功");
    }

    @DeleteMapping
    public AjaxResult remove(@Validated @RequestBody MachineRemoveCmd removeCmd) {
        receiveService.remove(removeCmd);
        return AjaxResult.success();
    }

    @PostMapping("/import")
    public AjaxResult importReceive(MultipartFile file) {
        log.info("Receive machine receive import request");
        // 将表格转为VO
        List<MachineReceiveImportVO> importVOs = new ExcelUtil<>(MachineReceiveImportVO.class).toList(file);
        CollUtil.removeNull(importVOs);
        if (CollUtil.isEmpty(importVOs)) {
            throw new ServiceException("导入失败，数据为空");
        }
        log.info("Import content size: {}", importVOs.size());
        importVOs.removeIf(a -> {
            // 校验表格数据
            String validateResult = ValidationUtils.validate(a);
            if (Objects.nonNull(validateResult)) {
                log.info(StrUtil.format("导入跳过，错误信息：{}，错误内容：{}", validateResult, a));
                return true;
            }
            return false;
        });
        log.info("收货单校验后数据剩余：{}", importVOs.size());
        // 处理订单的导入
        List<MachineReceiveImportCmd.Part> parts = new ArrayList<>();
        for (MachineReceiveImportVO importVO : importVOs) {
            MachineReceiveImportCmd.Part part;
            try {
                part = receiveAssemble.toPartImportCommand(importVO);
            } catch (ServiceException e) {
                log.info("收货单导入格式有误：{}。参数：{}", e.getMessage(), importVO);
                continue;
            }
            parts.add(part);
        }
        if (CollUtil.isEmpty(parts)) {
            throw new ServiceException("导入失败，数据为空");
        }
        log.info("开始导入，数据大小：{}", parts.size());
        MachineReceiveImportCmd importCommand = MachineReceiveImportCmd.builder().parts(parts).build();
        receiveService.importOrder(importCommand);
        return AjaxResult.success("收货单导入中,请稍后查询，勿重复导入！");
    }

    @PostMapping("/export")
    public void exportData(HttpServletResponse response, @RequestBody @Validated MachineOrderExportQuery query) {
        List<MachineReceiveExportR> list = receiveService.selectExportDataList(query);
        ExcelUtil<MachineReceiveExportR> util = new ExcelUtil<>(MachineReceiveExportR.class);
        util.exportExcel(response, list, StrUtil.format("{}收货单", query.getMonth()));
    }

    @PostMapping("/print")
    public AjaxResult print(@RequestBody MachineExportQuery query) {
        log.info("开始打印收货单");
        SysFile file = receiveService.print(query.getSerialNo());
        return AjaxResult.success(file);
    }

    @PostMapping("/record/export")
    public AjaxResult exportRecord(@RequestBody MachineRecordQuery query) {
        List<MachineReceiveRecordExportR> list = receiveService.exportRecord(query);
        String fileName = "收货记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineReceiveRecordExportR.class).sheet(fileName).doWrite(list);
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
