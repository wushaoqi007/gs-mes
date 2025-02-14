package com.greenstone.mes.material.event.listener;

import com.greenstone.mes.material.domain.service.CheckRecordService;
import com.greenstone.mes.material.domain.service.PartReceiveService;
import com.greenstone.mes.material.event.StockOperationEvent;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockOperationListener implements ApplicationListener<StockOperationEvent> {

    private PartStageStatusManager stageStatusManager;

    private CheckRecordService checkRecordService;

    private PartReceiveService partReceiveService;

    public StockOperationListener(PartStageStatusManager stageStatusManager, CheckRecordService checkRecordService,
                                  PartReceiveService partReceiveService) {
        this.stageStatusManager = stageStatusManager;
        this.checkRecordService = checkRecordService;
        this.partReceiveService = partReceiveService;
    }

    @Override
    public void onApplicationEvent(StockOperationEvent event) {
        StockOperationEventData operationEventData = event.getSource();
        // 零件阶段状态记录
        stageStatusManager.savePartStageStatus(operationEventData);
        // 保存表处记录，需要返工、需要表处、合格品入库时保存
        checkRecordService.saveAfterStockOperation(operationEventData);
        // 取件记录
        partReceiveService.saveOrUpdateAfterStockOperation(operationEventData);
    }

}
