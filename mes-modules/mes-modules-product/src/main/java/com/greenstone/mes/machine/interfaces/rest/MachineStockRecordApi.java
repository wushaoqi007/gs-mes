package com.greenstone.mes.machine.interfaces.rest;

import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.machine.application.dto.result.MachineStockRecordExportR;
import com.greenstone.mes.machine.application.service.MachineStockRecordService;
import com.greenstone.mes.machine.domain.service.MachineStockRecordManager;
import com.greenstone.mes.material.request.StockRecordDetailListReq;
import com.greenstone.mes.material.request.StockRecordMaterialSearchReq;
import com.greenstone.mes.material.request.StockRecordSearchReq;
import com.greenstone.mes.material.response.StockRecordDetailListResp;
import com.greenstone.mes.material.response.StockRecordMaterialSearchResp;
import com.greenstone.mes.material.response.StockRecordSearchResp;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 物料出入库记录Controller
 *
 * @author gu_renkai
 * @date 2022-02-17
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stock/record")
public class MachineStockRecordApi extends BaseController {

    private final MachineStockRecordService materialStockRecordService;

    private final MachineStockRecordManager stockRecordManager;

    private final RemoteFileService fileService;

    /**
     * 按存取记录查询出入库记录
     */
    @GetMapping("/list")
    public TableDataInfo list(StockRecordSearchReq searchReq) {
        startPage();
        List<StockRecordSearchResp> list = materialStockRecordService.listStockRecord(searchReq);
        return getDataTable(list);
    }

    /**
     * 按物料查询出入库记录
     */
    @GetMapping("/list/material")
    public TableDataInfo listByMaterial(StockRecordMaterialSearchReq searchReq) {
        startPage();
        List<StockRecordMaterialSearchResp> list = materialStockRecordService.listStockRecordMaterial(searchReq);
        return getDataTable(list);
    }

    @PostMapping("/list/material/export")
    public AjaxResult exportStockRecord(@RequestBody StockRecordMaterialSearchReq searchReq) {
        List<MachineStockRecordExportR> list = materialStockRecordService.exportStockRecord(searchReq);
        String fileName = "零件转移记录";
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineStockRecordExportR.class).sheet(fileName).doWrite(list);
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

    /**
     * 查询出入库记录明细
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable(value = "id") Long recordId) {
        return AjaxResult.success(stockRecordManager.getRecordDetail(recordId));
    }

    /**
     * 查询出入库记录明细列表
     */
    @GetMapping("/detail/list")
    public TableDataInfo detailList(StockRecordDetailListReq req) {
        startPage();
        List<StockRecordDetailListResp> list = materialStockRecordService.listStockRecordDetail(req);
        return getDataTable(list);
    }

}