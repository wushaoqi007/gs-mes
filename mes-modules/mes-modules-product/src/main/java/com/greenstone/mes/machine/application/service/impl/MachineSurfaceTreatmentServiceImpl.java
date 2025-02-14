package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineSurfaceTreatmentAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSurfaceTreatmentAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.event.MachineSurfaceTreatmentE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckPartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecord;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineSurfaceTreatmentResult;
import com.greenstone.mes.machine.application.event.MachineSurfaceTreatmentEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.application.service.MachineSurfaceTreatmentService;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatment;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentDetail;
import com.greenstone.mes.machine.domain.entity.MachineSurfaceTreatmentStage;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineCheckRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineSurfaceTreatmentRepository;
import com.greenstone.mes.machine.domain.repository.MachineSurfaceTreatmentStageRepository;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
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
import java.util.Objects;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineSurfaceTreatmentServiceImpl implements MachineSurfaceTreatmentService {

    private final MachineSurfaceTreatmentRepository surfaceTreatmentRepository;
    private final MachineSurfaceTreatmentAssemble surfaceTreatmentAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineSurfaceTreatmentStageRepository surfaceTreatmentStageRepository;
    private final MachineCheckRepository checkRepository;
    private final MachineOrderOldRepository orderRepository;
    private final MachineHelper machineHelper;
    private final StockVoHelper stockVoHelper;
    private final MachineStockService stockService;

    @Transactional
    @Override
    public void saveDraft(MachineSurfaceTreatmentAddCmd addCmd) {
        log.info("machine surface treatment save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineSurfaceTreatment surfaceTreatment = validAndAssembleSurfaceTreatment(addCmd, isNew, false);
        surfaceTreatment.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            surfaceTreatmentRepository.add(surfaceTreatment);
        } else {
            surfaceTreatmentRepository.edit(surfaceTreatment);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineSurfaceTreatmentAddCmd addCmd) {
        log.info("machine surface treatment save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineSurfaceTreatment surfaceTreatment = validAndAssembleSurfaceTreatment(addCmd, isNew, true);
        surfaceTreatment.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            surfaceTreatmentRepository.add(surfaceTreatment);
        } else {
            surfaceTreatmentRepository.edit(surfaceTreatment);
        }
        // 表处后操作
        eventPublisher.publishEvent(new MachineSurfaceTreatmentEvent(surfaceTreatmentAssemble.toSurfaceTreatmentE(surfaceTreatment)));
    }

    public MachineSurfaceTreatment validAndAssembleSurfaceTreatment(MachineSurfaceTreatmentAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineSurfaceTreatment surfaceTreatment = surfaceTreatmentAssemble.toMachineSurfaceTreatment(addCmd);
        if (isCommit) {
            for (MachineSurfaceTreatmentDetail part : surfaceTreatment.getParts()) {
                // 校验零件
                machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
                // 校验仓库
                machineHelper.existWarehouseByCode(part.getWarehouseCode());
                if (!addCmd.isForceOperation()) {
                    // 查询库存
                    Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), part.getWarehouseCode());
                    if (stockNumber < part.getHandleNumber()) {
                        throw new ServiceException(StrUtil.format("待表处数量不足，请检查质检单，项目号：{}，零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                                part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getHandleNumber()));
                    }
                }
            }
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_surfaceTreatment").prefix("MST" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            surfaceTreatment.setSerialNo(serialNoR.getSerialNo());
        }
        surfaceTreatment.getParts().forEach(p -> {
            p.setProvider(surfaceTreatment.getProvider());
            p.setSurfaceTreatment(surfaceTreatment.getSurfaceTreatment());
            p.setSerialNo(surfaceTreatment.getSerialNo());
        });
        setApplyInfo(surfaceTreatment);
        return surfaceTreatment;
    }

    public void surfaceTreatmentCheck(MachineSurfaceTreatmentDetail part, String surfaceTreatment) {
        // 校验表处顺序
        MachineSurfaceTreatmentStage surfaceTreatmentStage = surfaceTreatmentStageRepository.getByCheckDetailId(part.getCheckDetailId());
        if (Objects.isNull(surfaceTreatmentStage)) {
            throw new ServiceException(MachineError.E200308, StrUtil.format("质检单号：{}，详情id:{},零件号/版本：{}/{}", part.getCheckSerialNo(), part.getCheckDetailId(), part.getPartCode(), part.getPartVersion()));
        }
        if (!surfaceTreatment.equals(surfaceTreatmentStage.getStageName())) {
            throw new ServiceException(MachineError.E200309, StrUtil.format("质检单号：{}，详情id:{}，零件号/版本：{}/{}，当前应进行的处理方式：{}", part.getCheckSerialNo(), part.getCheckDetailId(), part.getPartCode(), part.getPartVersion(), surfaceTreatmentStage.getStageName()));
        }
        part.setSurfaceTreatment(surfaceTreatment);
    }

    public MachineCheckPartStockR scanPart(MachineSurfaceTreatmentDetail part) {
        // 校验仓库
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(part.getWarehouseCode());
        // 校验订单
        MachineOrderDetail orderDetail = orderRepository.selectPart(MachineOrderPartScanQuery.builder().serialNo(part.getOrderSerialNo())
                .requirementSerialNo(part.getRequirementSerialNo()).projectCode(part.getProjectCode())
                .partCode(part.getPartCode()).partVersion(part.getPartVersion()).build());
        // 校验质检单
        MachineCheckPartStockR checkPartStockR = checkRepository.scanPart(MachineCheckPartScanQuery.builder().orderSerialNo(part.getOrderSerialNo()).projectCode(part.getProjectCode())
                .partCode(part.getPartCode()).partVersion(part.getPartVersion()).checkResultType(CheckResultType.TREAT_SURFACE).build());
        // 获取库存数量
        checkPartStockR.setStockNumber(machineHelper.getStockNumber(orderDetail.getMaterialId(), part.getWarehouseCode()));
        checkPartStockR.setWarehouseId(warehouse.getId());
        checkPartStockR.setWarehouseName(warehouse.getName());
        checkPartStockR.setProvider(orderDetail.getProvider());
        checkPartStockR.setRequirementSerialNo(orderDetail.getRequirementSerialNo());
        return checkPartStockR;
    }

    @Override
    public MachineCheckPartStockR scan(MachineSurfaceTreatmentPartScanQuery query) {
        log.info("scan part from machine surface treatment params:{}", query);
        if (StrUtil.isBlank(query.getRequirementSerialNo()) && StrUtil.isBlank(query.getOrderSerialNo())) {
            throw new ServiceException("机加工需求单号和订单号不能都为空");
        }
        MachineSurfaceTreatmentDetail part = MachineSurfaceTreatmentDetail.builder().orderSerialNo(query.getOrderSerialNo())
                .requirementSerialNo(query.getRequirementSerialNo()).projectCode(query.getProjectCode())
                .partCode(query.getPartCode()).partVersion(query.getPartVersion()).warehouseCode(query.getWarehouseCode()).build();
        return scanPart(part);
    }

    @Override
    public List<MachineCheckPartStockR> partChoose(MachineCheckPartListQuery query) {
        log.info("part choose params:{}", query);
        query.setCheckResultType(CheckResultType.TREAT_SURFACE.getCode());
        return surfaceTreatmentAssemble.toMachineCheckPartStockRs(checkRepository.selectPartList(query));
    }

    @Override
    public List<MachineSurfaceTreatmentRecordExportR> exportRecord(MachineRecordQuery query) {
        return surfaceTreatmentAssemble.toMachineSurfaceTreatmentRecordERS(listRecord(query));
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        surfaceTreatmentRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineSurfaceTreatmentResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine surfaceTreatment list params:{}", query);
        List<MachineSurfaceTreatment> list = surfaceTreatmentRepository.list(query);
        return surfaceTreatmentAssemble.toMachineSurfaceTreatmentRs(list);
    }


    @Override
    public MachineSurfaceTreatmentResult detail(String serialNo) {
        log.info("query machine surfaceTreatment detail params:{}", serialNo);
        MachineSurfaceTreatment detail = surfaceTreatmentRepository.detail(serialNo);
        return surfaceTreatmentAssemble.toMachineSurfaceTreatmentR(detail);
    }

    @Override
    public List<MachineSurfaceTreatmentRecord> listRecord(MachineRecordQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(cn.hutool.core.date.DateUtil.endOfDay(query.getEndDate()));
        }
        return surfaceTreatmentRepository.listRecord(query);
    }

    @Override
    public void doStockWhenTreatCommit(MachineSurfaceTreatmentE source) {
        log.info("表处，开始处理库存数据: {}", source);
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(source);
        stockService.doStock(stockPrepareCmd);
        log.info("表处，处理库存数据完成");
    }

    public void setApplyInfo(MachineSurfaceTreatment surfaceTreatment) {
        if (StrUtil.isEmpty(surfaceTreatment.getSponsor())) {
            surfaceTreatment.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
            surfaceTreatment.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
            surfaceTreatment.setSponsorNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        }
        surfaceTreatment.setHandleTime(LocalDateTime.now());
    }
}
