package com.greenstone.mes.asset.application.service.impl;

import com.greenstone.mes.asset.application.assembler.AssetAssembler;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetClearE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetEditE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRequisitionE;
import com.greenstone.mes.asset.application.dto.cqe.event.AssetRevertE;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetHandleLogQuery;
import com.greenstone.mes.asset.application.dto.result.AssetHandleLogR;
import com.greenstone.mes.asset.application.service.AssetHandleLogService;
import com.greenstone.mes.asset.domain.entity.Asset;
import com.greenstone.mes.asset.domain.entity.AssetHandleLog;
import com.greenstone.mes.asset.domain.entity.AssetReqsDetail;
import com.greenstone.mes.asset.domain.repository.AssetHandleLogRepository;
import com.greenstone.mes.asset.infrastructure.enums.AssetHandleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:19
 */
@Service
public class AssetHandleLogServiceImpl implements AssetHandleLogService {

    private final AssetHandleLogRepository assetHandleLogRepository;

    private final AssetAssembler assetAssembler;

    public AssetHandleLogServiceImpl(AssetHandleLogRepository assetHandleLogRepository, AssetAssembler assetAssembler) {
        this.assetHandleLogRepository = assetHandleLogRepository;
        this.assetAssembler = assetAssembler;
    }

    @Override
    public List<AssetHandleLogR> list(AssetHandleLogQuery query) {
        List<AssetHandleLog> handleLogs = assetHandleLogRepository.list(query.getBarCode());
        return assetAssembler.toAssetHandleLogRs(handleLogs);
    }

    @Transactional
    @Override
    public void requisitionEvent(AssetRequisitionE event) {
        List<AssetHandleLog> assetHandleLogList = new ArrayList<>();
        for (AssetReqsDetail asset : event.getAssets()) {
            AssetHandleLog handleLog = AssetHandleLog.builder().barCode(asset.getBarCode())
                    .handleTime(event.getReceivedTime())
                    .handleType(AssetHandleType.REQUISITION)
                    .billId(event.getBillId())
                    .handlerId(event.getOperatedId())
                    .handlerName(event.getOperatedBy())
                    .content(event.getChangeContent()).build();
            assetHandleLogList.add(handleLog);
        }
        assetHandleLogRepository.save(assetHandleLogList);
    }

    @Transactional
    @Override
    public void revertEvent(AssetRevertE event) {
        List<AssetHandleLog> assetHandleLogList = new ArrayList<>();
        for (Asset asset : event.getAssets()) {
            AssetHandleLog handleLog = AssetHandleLog.builder().barCode(asset.getBarCode())
                    .handleTime(event.getRevertedTime())
                    .handleType(AssetHandleType.REVERT)
                    .billId(event.getBillId())
                    .handlerId(event.getOperatedId())
                    .handlerName(event.getOperatedBy())
                    .content(event.getChangeContent()).build();
            assetHandleLogList.add(handleLog);
        }
        assetHandleLogRepository.save(assetHandleLogList);
    }

    @Override
    public void editEvent(AssetEditE event) {
        AssetHandleLog handleLog = AssetHandleLog.builder().barCode(event.getBarCode())
                .handleTime(event.getEditedTime())
                .handleType(AssetHandleType.EDIT)
                .handlerId(event.getEditedBy())
                .handlerName(event.getEditedByName())
                .content(event.getChangeContent()).build();
        assetHandleLogRepository.save(handleLog);
    }

    @Override
    public void clearEvent(AssetClearE event) {
        List<AssetHandleLog> assetHandleLogList = new ArrayList<>();
        for (Asset asset : event.getAssets()) {
            AssetHandleLog handleLog = AssetHandleLog.builder().barCode(asset.getBarCode())
                    .handleTime(event.getClearTime())
                    .handleType(event.isRestore() ? AssetHandleType.CLEAR : AssetHandleType.RESTORE)
                    .handlerId(event.getBillId())
                    .handlerName(event.getClearByName())
                    .content(event.getChangeContent()).build();
            assetHandleLogList.add(handleLog);
        }
        assetHandleLogRepository.save(assetHandleLogList);
    }
}
