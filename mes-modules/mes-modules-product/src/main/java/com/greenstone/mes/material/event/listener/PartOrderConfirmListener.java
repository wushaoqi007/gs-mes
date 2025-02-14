package com.greenstone.mes.material.event.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.material.application.assembler.WorksheetAssembler;
import com.greenstone.mes.material.constant.PurchaseConstant;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.event.PartOrderConfirmEvent;
import com.greenstone.mes.material.event.data.ConfirmEventData;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class PartOrderConfirmListener implements ApplicationListener<PartOrderConfirmEvent> {

    @Autowired
    private WorksheetAssembler worksheetAssembler;

    @Autowired
    private RemoteBomService bomService;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Override
    public void onApplicationEvent(PartOrderConfirmEvent event) {
        log.info("Receive part order confirm event.");
        ConfirmEventData source = event.getSource();

        List<BomImportDTO> bomImportDTOs = new ArrayList<>();
        for (ProcessOrderDetailDO processOrderDetailDO : source.getProcessOrderDetailDOList()) {
            ProcessOrderDetailDO detail = worksheetDetailService.getById(processOrderDetailDO.getId());
            if (!Objects.isNull(detail) && !PurchaseConstant.PurchasePartStatus.ABANDON.equals(detail.getStatus())) {
                bomImportDTOs.add(worksheetAssembler.toBomImportDto(source.getProcessOrderDO(), detail));
            }
        }
        // 保存更新bom
        if (CollectionUtil.isNotEmpty(bomImportDTOs)) {
            log.info("Start save bom , size:{}", bomImportDTOs.size());
            bomService.addBomListByImport(bomImportDTOs);
        } else {
            log.info("No need to save bom");
        }
    }

}
