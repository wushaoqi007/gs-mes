package com.greenstone.mes.material.domain.service.impl;

import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.assembler.PartReceiveAssembler;
import com.greenstone.mes.material.domain.entity.PartReceive;
import com.greenstone.mes.material.domain.entity.PartReceiveRecord;
import com.greenstone.mes.material.domain.repository.PartReceiveRepository;
import com.greenstone.mes.material.domain.service.PartReceiveService;
import com.greenstone.mes.material.dto.PartReceiveEditCommand;
import com.greenstone.mes.material.dto.PartReceiveSaveCommand;
import com.greenstone.mes.material.dto.cmd.PartReceiveListQuery;
import com.greenstone.mes.material.dto.cmd.PartReceiveRecordListQuery;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.interfaces.response.PartReceiveR;
import com.greenstone.mes.material.interfaces.response.PartReceiveRecordR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PartReceiveServiceImpl implements PartReceiveService {

    private final PartReceiveRepository partReceiveRepository;
    private final PartReceiveAssembler partReceiveAssembler;

    public PartReceiveServiceImpl(PartReceiveRepository partReceiveRepository, PartReceiveAssembler partReceiveAssembler) {
        this.partReceiveRepository = partReceiveRepository;
        this.partReceiveAssembler = partReceiveAssembler;
    }


    @Override
    public void save(PartReceiveSaveCommand saveCommand) {
        List<PartReceive> partReceives = partReceiveAssembler.toPartReceives(saveCommand.getPartInfoList());
        partReceiveRepository.save(partReceives);
    }

    private void update(PartReceiveEditCommand editCmd) {
        List<PartReceive> partReceives = partReceiveAssembler.toPartReceives(editCmd);
        partReceiveRepository.update(partReceives);
    }

    @Override
    public List<PartReceiveRecordR> recordList(PartReceiveRecordListQuery listQuery) {
        listQuery.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        List<PartReceiveRecord> partReceiveRecordList = partReceiveRepository.recordList(listQuery);
        return partReceiveAssembler.toPartReceiveRecordListR(partReceiveRecordList);
    }

    @Override
    public List<PartReceiveR> listPartsByRecordId(PartReceiveListQuery listQuery) {
        List<PartReceive> partReceiveList = partReceiveRepository.listPartsByRecordId(listQuery.getRecordId());
        return partReceiveAssembler.toPartReceiveRs(partReceiveList);
    }

    @Override
    public void saveOrUpdateAfterStockOperation(StockOperationEventData operationEventData) {
        BillOperation operation = operationEventData.getOperation();
//        if (operation.equals(PartOperation.CHECK) && operationEventData.getAction().equals(StockAction.OUT)) {
//            PartReceiveSaveCommand saveCommand = partReceiveAssembler.toPartReceiveSaveCommand(operationEventData.getMaterialList());
//            save(saveCommand);
//        }
//        if (operation.isInStockAfterCheck() && operationEventData.getPartsGroupId() != null && operationEventData.getAction().equals(StockAction.IN)) {
//            PartReceiveEditCommand editCmd = partReceiveAssembler.toPartReceiveEditCmd(operationEventData.getMaterialList(), operationEventData);
//            update(editCmd);
//        }
    }

}
