package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.dto.PartReceiveSaveCommand;
import com.greenstone.mes.material.dto.cmd.PartReceiveListQuery;
import com.greenstone.mes.material.dto.cmd.PartReceiveRecordListQuery;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.interfaces.response.PartReceiveR;
import com.greenstone.mes.material.interfaces.response.PartReceiveRecordR;

import java.util.List;


public interface PartReceiveService {

    void save(PartReceiveSaveCommand saveCommand);

    List<PartReceiveRecordR> recordList(PartReceiveRecordListQuery listQuery);

    List<PartReceiveR> listPartsByRecordId(PartReceiveListQuery listQuery);

    void saveOrUpdateAfterStockOperation(StockOperationEventData operationEventData);
}
