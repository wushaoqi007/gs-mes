package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.dto.CheckRecordExportCommand;
import com.greenstone.mes.material.dto.CheckRecordListQuery;
import com.greenstone.mes.material.dto.CheckRecordSaveCommand;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.interfaces.response.CheckRecordListResp;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author gu_renkai
 * @date 2022/12/19 11:11
 */

public interface CheckRecordService {

    void saveAfterStockOperation(StockOperationEventData operationEventData);

    void save(CheckRecordSaveCommand saveCommand);

    PageList<CheckRecordListResp> list(CheckRecordListQuery listQuery);

    XSSFWorkbook export(CheckRecordExportCommand exportCommand);


}
