package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineStockChangeAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockChangeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.event.MachineStockChangeE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineStockChangeResult;
import com.greenstone.mes.machine.application.event.MachineStockChangeEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineStockChangeService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.entity.MachineStockChange;
import com.greenstone.mes.machine.domain.entity.MachineStockChangeDetail;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineStockChangeRepository;
import com.greenstone.mes.machine.domain.repository.MachineStockRepository;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineStockChangeServiceImpl implements MachineStockChangeService {

    private final MachineStockChangeRepository stockChangeRepository;
    private final MachineStockChangeAssemble stockChangeAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineStockRepository stockRepository;
    private final MachineHelper machineHelper;
    private final MachineStockService stockService;
    private final StockVoHelper stockVoHelper;

    @Transactional
    @Override
    public void saveDraft(MachineStockChangeAddCmd addCmd) {
        log.info("machine stock change save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineStockChange stockChange = validAndAssembleStockChange(addCmd, isNew, false);
        stockChange.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            stockChangeRepository.add(stockChange);
        } else {
            stockChangeRepository.edit(stockChange);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineStockChangeAddCmd addCmd) {
        log.info("machine stock change save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineStockChange stockChange = validAndAssembleStockChange(addCmd, isNew, true);
        stockChange.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            stockChangeRepository.add(stockChange);
        } else {
            stockChangeRepository.edit(stockChange);
        }
        eventPublisher.publishEvent(new MachineStockChangeEvent(stockChangeAssemble.toStockChangeE(stockChange)));
    }

    public MachineStockChange validAndAssembleStockChange(MachineStockChangeAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineStockChange stockChange = stockChangeAssemble.toMachineStockChange(addCmd);
        setApplyInfo(stockChange);
        if (isCommit) {
            for (MachineStockChangeDetail part : stockChange.getParts()) {
                // 校验零件
                machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
                // 校验仓库
                BaseWarehouse warehouse = machineHelper.existWarehouseByCode(part.getWarehouseCode());
                // 查询库存
                Long stockNum = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), warehouse.getCode());
                part.setStockNumber(stockNum);
                // 此处不可随意删除，事件变更库存数量需要使用
                part.setWarehouseId(warehouse.getId());
            }
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_stockChange").prefix("MSC" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            stockChange.setSerialNo(serialNoR.getSerialNo());
        }
        stockChange.getParts().forEach(p -> p.setSerialNo(stockChange.getSerialNo()));
        return stockChange;
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        stockChangeRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineStockChangeResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine stockChange list params:{}", query);
        List<MachineStockChange> list = stockChangeRepository.list(query);
        return stockChangeAssemble.toMachineStockChangeRs(list);
    }


    @Override
    public MachineStockChangeResult detail(String serialNo) {
        log.info("query machine stockChange detail params:{}", serialNo);
        MachineStockChange detail = stockChangeRepository.detail(serialNo);
        return stockChangeAssemble.toMachineStockChangeR(detail);
    }

    @Override
    public MachinePartStockR scan(MachinePartStockScanQuery query) {
        // 校验物料
        BaseMaterial baseMaterial = machineHelper.checkMaterial(query.getPartCode(), query.getPartVersion());
        MachinePartStockR partStockR = stockChangeAssemble.toPartStockR(baseMaterial);
        // 校验仓库
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
        partStockR.setWarehouseId(warehouse.getId());
        partStockR.setWarehouseCode(warehouse.getCode());
        partStockR.setWarehouseName(warehouse.getName());
        // 库存查询
        partStockR.setStockNumber(machineHelper.getStockNumber(baseMaterial.getId(), query.getWarehouseCode()));
        return partStockR;
    }

    @Override
    public List<MachinePartStockR> partChoose(MachinePartStockQuery query) {
        return stockRepository.listStock(query);
    }

    @Override
    public List<MachineStockChangeRecord> listRecord(MachineRecordQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(cn.hutool.core.date.DateUtil.endOfDay(query.getEndDate()));
        }
        return stockChangeRepository.listRecord(query);
    }

    @Override
    public List<MachineStockChangeRecordExportR> exportRecord(MachineRecordQuery query) {
        return stockChangeAssemble.toMachineStockChangeRecordERS(listRecord(query));
    }

    @Override
    public void operationAfterStockChange(MachineStockChangeE source) {
        // 修改库存
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(source);
        stockService.doStock(stockPrepareCmd);
    }

    public void setApplyInfo(MachineStockChange stockChange) {
        if (StrUtil.isEmpty(stockChange.getChangedBy())) {
            stockChange.setChangedById(SecurityUtils.getLoginUser().getUser().getUserId());
            stockChange.setChangedBy(SecurityUtils.getLoginUser().getUser().getNickName());
            stockChange.setChangedByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        }
        stockChange.setChangeTime(LocalDateTime.now());
    }
}
