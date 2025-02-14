package com.greenstone.mes.material.event.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.application.assembler.WorksheetAssembler;
import com.greenstone.mes.material.domain.entity.ProcessOrder;
import com.greenstone.mes.material.event.PartOrderSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PartOrderSaveListener implements ApplicationListener<PartOrderSaveEvent> {

    @Autowired
    private WorksheetAssembler worksheetAssembler;

    @Autowired
    private RemoteBomService bomService;

    @Override
    public void onApplicationEvent(PartOrderSaveEvent event) {
        log.info("Receive part order import event.");
        ProcessOrder processOrder = event.getSource();

        List<BomImportDTO> bomImportDTOs = new ArrayList<>();
        for (ProcessOrder.ProcessComponent component : processOrder.getComponents()) {
            for (ProcessOrder.ProcessPart part : component.getParts()) {
                if (part.getReason().needUpdateBom()) {
                    bomImportDTOs.add(worksheetAssembler.toBomImportDto(processOrder, component, part));
                }
            }
        }

        // 保存更新bom
        if (CollectionUtil.isNotEmpty(bomImportDTOs)) {
            log.info("Start save bom and material, size:{}", bomImportDTOs.size());
            R<String> r = bomService.addBomListByImport(bomImportDTOs);
            if (r.isFail()) {
                throw new RuntimeException(r.getMsg());
            }
        } else {
            log.info("No need to save bom");
        }
    }

}
