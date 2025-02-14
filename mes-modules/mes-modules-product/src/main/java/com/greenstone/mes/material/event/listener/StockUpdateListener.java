package com.greenstone.mes.material.event.listener;

import com.greenstone.mes.material.application.dto.StockRecordSaveCommand;
import com.greenstone.mes.material.application.assembler.StockAssembler;
import com.greenstone.mes.material.event.StockUpdateEvent;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.application.service.MaterialStockRecordManager;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockUpdateListener implements ApplicationListener<StockUpdateEvent> {

    private MaterialStockRecordManager stockRecordManager;
    private StockAssembler stockAssembler;
    private PartStageStatusManager stageStatusManager;

    public StockUpdateListener(MaterialStockRecordManager stockRecordManager,
                               PartStageStatusManager stageStatusManager, StockAssembler stockAssembler) {
        this.stockRecordManager = stockRecordManager;
        this.stageStatusManager = stageStatusManager;
        this.stockAssembler = stockAssembler;
    }

    @Override
    public void onApplicationEvent(StockUpdateEvent event) {
        StockUpdateEventData stockUpdateEventData = event.getSource();
        StockRecordSaveCommand stockRecordSaveCommand = stockAssembler.toStockRecordUpdateCommand(stockUpdateEventData);
        // 零件阶段状态修改
        stageStatusManager.updatePartStageStatus(stockUpdateEventData);

        // 保存出入库(更改)记录
        stockRecordManager.saveStockRecord(stockRecordSaveCommand);
    }

}
