package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineReworkAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReworkAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.event.MachineReworkE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineReworkRecord;
import com.greenstone.mes.machine.application.dto.result.MachineReworkResult;
import com.greenstone.mes.machine.application.event.MachineReworkEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineReworkService;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineRework;
import com.greenstone.mes.machine.domain.entity.MachineReworkDetail;
import com.greenstone.mes.machine.domain.repository.MachineCheckRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineReworkRepository;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineReworkServiceImpl implements MachineReworkService {

    private final MachineReworkRepository reworkRepository;
    private final MachineReworkAssemble reworkAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineStockManager stockManager;
    private final MachineCheckRepository checkRepository;
    private final MachineOrderOldRepository orderRepository;
    private final MachineHelper machineHelper;

    @Transactional
    @Override
    public void saveDraft(MachineReworkAddCmd addCmd) {
        log.info("machine rework save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineRework rework = validAndAssembleRework(addCmd, isNew, false);
        rework.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            reworkRepository.add(rework);
        } else {
            reworkRepository.edit(rework);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineReworkAddCmd addCmd) {
        log.info("machine rework save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineRework rework = validAndAssembleRework(addCmd, isNew, true);
        rework.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            reworkRepository.add(rework);
        } else {
            reworkRepository.edit(rework);
        }
        // 返工后操作
        eventPublisher.publishEvent(new MachineReworkEvent(reworkAssemble.toReworkE(rework)));
    }

    public MachineRework validAndAssembleRework(MachineReworkAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineRework rework = reworkAssemble.toMachineRework(addCmd);
        if (isCommit) {
            for (MachineReworkDetail part : rework.getParts()) {
                // 校验零件
                machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
                // 校验仓库
                machineHelper.existWarehouseByCode(part.getWarehouseCode());
                if (!addCmd.isForceOperation()) {
                    // 查询库存
                    Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), part.getWarehouseCode());
                    if (stockNumber < part.getReworkNumber()) {
                        throw new ServiceException(MachineError.E200014, StrUtil.format("项目号：{}，零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                                part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getReworkNumber()));
                    }
                }
            }
        }

        // 设置单号
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_rework").prefix("MRW" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            rework.setSerialNo(serialNoR.getSerialNo());
        }
        rework.getParts().forEach(p -> p.setSerialNo(rework.getSerialNo()));
        setApplyInfo(rework);
        return rework;
    }


    public MachineCheckPartStockR scanPart(MachineReworkDetail part) {
        // 校验仓库
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(part.getWarehouseCode());
        // 校验订单
        MachineOrderDetail orderDetail = orderRepository.selectPart(MachineOrderPartScanQuery.builder().serialNo(part.getOrderSerialNo())
                .requirementSerialNo(part.getRequirementSerialNo()).projectCode(part.getProjectCode())
                .partCode(part.getPartCode()).partVersion(part.getPartVersion()).build());
        // 校验质检单
        // TODO 不校验质检单了，从车间直接拿过来返工也行
        MachineCheckPartStockR partStockR = checkRepository.scanPart(MachineCheckPartScanQuery.builder().orderSerialNo(part.getOrderSerialNo()).projectCode(part.getProjectCode())
                .partCode(part.getPartCode()).partVersion(part.getPartVersion()).checkResultType(CheckResultType.REWORK).build());
        // 获取库存数量
        partStockR.setStockNumber(machineHelper.getStockNumber(orderDetail.getMaterialId(), part.getWarehouseCode()));
        partStockR.setWarehouseId(warehouse.getId());
        partStockR.setWarehouseName(warehouse.getName());
        partStockR.setProvider(orderDetail.getProvider());
        partStockR.setRequirementSerialNo(orderDetail.getRequirementSerialNo());
        return partStockR;
    }

    @Override
    public MachineCheckPartStockR scan(MachineReworkPartScanQuery query) {
        log.info("scan part from machine rework params:{}", query);
        if (StrUtil.isBlank(query.getRequirementSerialNo()) && StrUtil.isBlank(query.getOrderSerialNo())) {
            throw new ServiceException("机加工需求单号和订单号不能都为空");
        }
        MachineReworkDetail part = MachineReworkDetail.builder().orderSerialNo(query.getOrderSerialNo())
                .requirementSerialNo(query.getRequirementSerialNo()).projectCode(query.getProjectCode())
                .partCode(query.getPartCode()).partVersion(query.getPartVersion()).warehouseCode(query.getWarehouseCode()).build();
        return scanPart(part);
    }

    @Override
    public List<MachineCheckPartStockR> partChoose(MachineCheckPartListQuery query) {
        log.info("part choose params:{}", query);
        query.setCheckResultType(CheckResultType.REWORK.getCode());
        return reworkAssemble.toMachineCheckPartStockRs(checkRepository.selectPartList(query));
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        reworkRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineReworkResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine rework list params:{}", query);
        List<MachineRework> list = reworkRepository.list(query);
        return reworkAssemble.toMachineReworkRs(list);
    }

    @Override
    public MachineReworkResult detail(String serialNo) {
        log.info("query machine rework detail params:{}", serialNo);
        MachineRework detail = reworkRepository.detail(serialNo);
        return reworkAssemble.toMachineReworkR(detail);
    }

    @Override
    public List<MachineReworkRecord> listRecord(MachineRecordFuzzyQuery query) {
        return reworkRepository.listRecord(query);
    }

    @Override
    public void operationAfterRework(MachineReworkE source) {
        log.info("operationAfterRework params:{}", source);
        Map<String, List<MachineReworkE.Part>> groupByWarehouse = source.getParts().stream().collect(Collectors.groupingBy(MachineReworkE.Part::getWarehouseCode));
        groupByWarehouse.forEach((warehouseCode, list) -> {
            BaseWarehouse warehouse = machineHelper.existWarehouseByCode(warehouseCode);
            List<MachineStockTransferVo.MaterialInfo> materialInfoList = new ArrayList<>();
            for (MachineReworkE.Part part : list) {
                MachineStockTransferVo.MaterialInfo material = MachineStockTransferVo.MaterialInfo.builder()
                        .orderSerialNo(part.getOrderSerialNo()).orderDetailId(part.getOrderDetailId())
                        .projectCode(part.getProjectCode()).materialId(part.getMaterialId())
                        .number(part.getReworkNumber()).build();
                materialInfoList.add(material);
            }
            MachineStockTransferVo stockTransferVo = MachineStockTransferVo.builder().operation(BillOperation.REWORK).inStockWhId(warehouse.getId()).remark("AUTO").sponsor(source.getSponsor()).materialInfoList(materialInfoList).build();
            log.info("rework transfer params:{}", stockTransferVo);
            stockManager.transfer(stockTransferVo);
        });
    }

    public void setApplyInfo(MachineRework rework) {
        rework.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
        rework.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
        rework.setSponsorNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        rework.setReworkTime(LocalDateTime.now());
    }
}
