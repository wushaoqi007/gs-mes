package com.greenstone.mes.material.event.listener;

import com.greenstone.mes.material.application.dto.StockRecordSaveCommand;
import com.greenstone.mes.material.application.assembler.StockAssembler;
import com.greenstone.mes.material.event.StockEvent;
import com.greenstone.mes.material.event.data.StockEventData;
import com.greenstone.mes.material.application.service.MaterialStockRecordManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockListener implements ApplicationListener<StockEvent> {

    private MaterialStockRecordManager stockRecordManager;

    private StockAssembler stockAssembler;

    public StockListener(MaterialStockRecordManager stockRecordManager, StockAssembler stockAssembler) {
        this.stockRecordManager = stockRecordManager;
        this.stockAssembler = stockAssembler;
    }

    @Override
    public void onApplicationEvent(StockEvent event) {
        StockEventData stockEventData = event.getSource();
        StockRecordSaveCommand stockRecordSaveCommand = stockAssembler.toStockRecordSaveCommand(stockEventData);
        // 保存出入库记录
        stockRecordManager.saveStockRecord(stockRecordSaveCommand);

        // 更新领料单信息
        // TODO 需要根据物料的项目代码来更新领料单
//        for (StockEventData.StockDetail stockDetail : stockEventData.getMaterialList()) {
//            if (stockDetail.getReceivingDetailId() != null) {
//                MaterialReceivingDetail receivingDetail = receivingDetailService.getById(stockDetail.getReceivingDetailId());
//                if (ObjectUtil.isEmpty(receivingDetail)) {
//                    log.error("未找到领料单详情:" + stockDetail.getReceivingDetailId());
//                    throw new ServiceException("未找到领料单详情:" + stockDetail.getReceivingDetailId());
//                }
//                int receivedTotal = receivingDetail.getReceivedNum() + stockDetail.getNumber().intValue();
//                if (receivingDetail.getTotalNum() < receivedTotal) {
//                    log.error("该领料单已领取完毕:" + receivingDetail.getTotalNum());
//                    throw new ServiceException("该领料单已领取完毕:" + receivingDetail.getTotalNum());
//                }
//                // 修改领料单出库数据
//                receivingDetail.setReceivedNum(receivedTotal);
//                receivingDetailService.updateById(receivingDetail);
//            }
//        }

    }

}
