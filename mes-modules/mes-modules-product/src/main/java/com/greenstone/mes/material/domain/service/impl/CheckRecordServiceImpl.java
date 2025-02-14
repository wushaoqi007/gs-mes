package com.greenstone.mes.material.domain.service.impl;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.application.helper.excel.CheckRecordExcelHelper;
import com.greenstone.mes.material.application.assembler.CheckRecordAssembler;
import com.greenstone.mes.material.domain.entity.CheckRecord;
import com.greenstone.mes.material.domain.repository.CheckRecordRepository;
import com.greenstone.mes.material.domain.service.CheckRecordService;
import com.greenstone.mes.material.dto.CheckRecordExportCommand;
import com.greenstone.mes.material.dto.CheckRecordListQuery;
import com.greenstone.mes.material.dto.CheckRecordSaveCommand;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.interfaces.response.CheckRecordListResp;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.FileRecord;
import com.greenstone.mes.file.api.request.FileUploadReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/19 13:18
 */

@Slf4j
@Service
public class CheckRecordServiceImpl implements CheckRecordService {

    private final CheckRecordRepository checkRecordRepository;
    private final CheckRecordAssembler checkRecordAssembler;
    private final RemoteFileService fileService;
    private final CheckRecordExcelHelper excelHelper;

    public CheckRecordServiceImpl(CheckRecordRepository checkRecordRepository, CheckRecordAssembler checkRecordAssembler,
                                  RemoteFileService fileService, CheckRecordExcelHelper excelHelper) {
        this.checkRecordRepository = checkRecordRepository;
        this.checkRecordAssembler = checkRecordAssembler;
        this.fileService = fileService;
        this.excelHelper = excelHelper;
    }

    @Override
    public void saveAfterStockOperation(StockOperationEventData operationEventData) {
        BillOperation operation = operationEventData.getOperation();
        if (operationEventData.getAction() == StockAction.IN && operation.isInStockAfterCheck()) {
            for (StockOperationEventData.StockMaterial stockMaterial : operationEventData.getMaterialList()) {
                BaseMaterial material = stockMaterial.getMaterial();
                CheckRecordSaveCommand checkRecordSaveCommand = checkRecordAssembler.toCheckRecord(operationEventData, material);
                if (operation == BillOperation.CHECKED_NG_CREATE) {
                    checkRecordSaveCommand.setResult(CheckResult.NG.getId());
                } else {
                    checkRecordSaveCommand.setResult(CheckResult.OK.getId());
                }
                save(checkRecordSaveCommand);
            }

        }
    }

    @Override
    public void save(CheckRecordSaveCommand saveCommand) {
        CheckRecord checkRecord = checkRecordAssembler.toCheckRecord(saveCommand);
        checkRecordRepository.save(checkRecord);

        FileUploadReq fileUploadReq = FileUploadReq.builder().fileList(saveCommand.getFiles()).relationId(checkRecord.getId()).relationType(3).build();
        fileService.uploadMultipart(fileUploadReq);
    }

    @Override
    public PageList<CheckRecordListResp> list(CheckRecordListQuery query) {
        PageList<CheckRecord> pageList = checkRecordRepository.list(query);
        List<CheckRecordListResp> checkRecordListRespList = checkRecordAssembler.toCheckRecordListRespList(pageList.getResultList());
        for (CheckRecordListResp resp : checkRecordListRespList) {
            if (resp.isHasImage()) {
                List<FileRecord> fileRecords = fileService.info(resp.getId(), 3).getData();
                List<CheckRecordListResp.Image> images = fileRecords.stream().map(f -> new CheckRecordListResp.Image(f.getUrl())).toList();
                resp.setImages(images);
            }
        }
        return PageList.of(pageList, checkRecordListRespList);
    }

    @Override
    public XSSFWorkbook export(CheckRecordExportCommand exportCommand) {
        CheckRecordListQuery query = checkRecordAssembler.toListQuery(exportCommand);
        List<CheckRecord> checkRecords = checkRecordRepository.list(query).getResultList();

        return excelHelper.makeCheckRecordExcel(checkRecords);
    }

}
