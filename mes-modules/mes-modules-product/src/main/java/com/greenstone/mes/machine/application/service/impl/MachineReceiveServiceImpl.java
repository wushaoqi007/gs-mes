package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineOrderAssemble;
import com.greenstone.mes.machine.application.assemble.MachineReceiveAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.*;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineReceiveService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.entity.*;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineCheckRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineReceiveRepository;
import com.greenstone.mes.machine.domain.repository.MachineSurfaceTreatmentRepository;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-12-08-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineReceiveServiceImpl implements MachineReceiveService {

    private final MachineReceiveRepository receiveRepository;
    private final MachineReceiveAssemble receiveAssemble;
    private final RemoteSystemService systemService;
    private final MachineStockManager stockManager;
    private final MachineOrderOldRepository orderRepository;
    private final MachineOrderAssemble orderAssemble;
    private final MachineHelper machineHelper;
    private final MachineCheckRepository checkRepository;
    private final MachineSurfaceTreatmentRepository surfaceTreatmentRepository;
    private final StockVoHelper stockVoHelper;
    private final MachineStockService stockService;

    @Transactional
    @Override
    public void saveDraft(MachineReceiveAddCmd addCmd) {
        log.info("machine receive save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineReceive receive = validAndAssembleReceive(addCmd, isNew, false);
        receive.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            receiveRepository.add(receive);
        } else {
            receiveRepository.edit(receive);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineReceiveAddCmd addCmd) {
        log.info("machine receive save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineReceive receive = validAndAssembleReceive(addCmd, isNew, true);
        receive.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            receiveRepository.add(receive);
        } else {
            receiveRepository.edit(receive);
        }
        // 收件零件转移操作
        doStockWhenReceiveCommit(receive);
        // 普通收货、返工收货更新订单收货数量
        orderReceive(addCmd);
        // 返工收货更新返工单收货数量
        reworkReceive(addCmd);
        // 表处收货更新表处单收货数量
        surfaceTreatmentReceive(addCmd);
    }

    public void orderReceive(MachineReceiveAddCmd addCmd) {
        List<MachineReceiveAddCmd.Part> normalParts = addCmd.getParts().stream().filter(p -> p.getOperation() == BillOperation.RECEIVE_CREATE.getId() || p.getOperation() == BillOperation.RECEIVE_REWORKED_CREATE.getId()).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(normalParts)) {
            Map<String, List<MachineReceiveAddCmd.Part>> groupByReSerialNo = normalParts.stream().collect(Collectors.groupingBy(MachineReceiveAddCmd.Part::getOrderSerialNo));
            groupByReSerialNo.forEach((orderSerialNo, list) -> {
                MachineOrder detail = orderRepository.detail(orderSerialNo);
                if (detail.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
                    throw new ServiceException(StrUtil.format("订单未生效，订单号：{}", orderSerialNo));
                }
                for (MachineReceiveAddCmd.Part part : list) {
                    // 收货必须要有订单
                    MachineOrderDetail updateOrderDetail = machineHelper.existInMachineOrder(detail, part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                    // 校验供应商
                    if (!updateOrderDetail.getProvider().equals(addCmd.getProvider())) {
                        throw new ServiceException(StrUtil.format("收货供应商和订单供应商不一致，订单供应商：{}，收货单供应商：{}，订单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", updateOrderDetail.getProvider(), addCmd.getProvider(), updateOrderDetail.getSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    // 普通收货和返工收货的更新订单收货数量
                    if (part.getOperation() == BillOperation.RECEIVE_CREATE.getId() || part.getOperation() == BillOperation.RECEIVE_REWORKED_CREATE.getId()) {
                        long receiveNum = updateOrderDetail.getReceivedNumber() == null ? part.getActualNumber() : updateOrderDetail.getReceivedNumber() + part.getActualNumber();
                        if (updateOrderDetail.getProcessNumber() < receiveNum) {
                            throw new ServiceException(StrUtil.format("超出订单购买数量，订单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", updateOrderDetail.getSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                        }
                        updateOrderDetail.setReceivedNumber(receiveNum);
                        updateOrderDetail.setReceiveTime(LocalDateTime.now());
                        orderRepository.update(updateOrderDetail);
                    }
                }
            });
        }

    }

    public void surfaceTreatmentReceive(MachineReceiveAddCmd addCmd) {
        List<MachineReceiveAddCmd.Part> surfaceTreatParts = addCmd.getParts().stream().filter(p -> p.getOperation() == BillOperation.RECEIVE_TREAT_CREATE.getId()).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(surfaceTreatParts)) {
            Optional<MachineReceiveAddCmd.Part> noSerialNo = surfaceTreatParts.stream().filter(s -> s.getSurfaceTreatmentSerialNo() == null).findFirst();
            if (noSerialNo.isPresent()) {
                MachineReceiveAddCmd.Part noSerialNoPart = noSerialNo.get();
                throw new ServiceException(StrUtil.format("表处收货的零件，表处单号不为空，申请单号：{}，项目号：{}，零件号/版本：{}/{}", noSerialNoPart.getRequirementSerialNo(), noSerialNoPart.getProjectCode(), noSerialNoPart.getPartCode(), noSerialNoPart.getPartVersion()));
            }
            Map<String, List<MachineReceiveAddCmd.Part>> groupBySerialNo = surfaceTreatParts.stream().collect(Collectors.groupingBy(MachineReceiveAddCmd.Part::getSurfaceTreatmentSerialNo));
            groupBySerialNo.forEach((serialNo, list) -> {
                MachineSurfaceTreatment detail = surfaceTreatmentRepository.detail(serialNo);
                for (MachineReceiveAddCmd.Part part : list) {
                    // 表处收货必须要有表处单
                    Optional<MachineSurfaceTreatmentDetail> findPart = detail.getParts().stream().filter(o -> o.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                            && o.getProjectCode().equals(part.getProjectCode())
                            && o.getPartCode().equals(part.getPartCode())
                            && o.getPartVersion().equals(part.getPartVersion())).findFirst();
                    if (findPart.isEmpty()) {
                        throw new ServiceException(StrUtil.format("表处收货的零件，未找到表处单。表处单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", part.getSurfaceTreatmentSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    MachineSurfaceTreatmentDetail updateDetail = findPart.get();
                    // 校验供应商
                    if (StrUtil.isNotEmpty(updateDetail.getProvider()) && !updateDetail.getProvider().equals(addCmd.getProvider())) {
                        throw new ServiceException(StrUtil.format("收货供应商和表处供应商不一致，表处供应商：{}，收货单供应商：{}，表处单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", updateDetail.getProvider(), addCmd.getProvider(), part.getSurfaceTreatmentSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    long receiveNum = updateDetail.getReceivedNumber() == null ? part.getActualNumber() : updateDetail.getReceivedNumber() + part.getActualNumber();
                    // 表处收货不能超过表处单未收货数量
                    if (receiveNum > updateDetail.getHandleNumber()) {
                        throw new ServiceException(StrUtil.format("表处收货的零件，超出表处单未收货数量。表处单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", part.getSurfaceTreatmentSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    updateDetail.setReceiveTime(LocalDateTime.now());
                    updateDetail.setReceivedNumber(receiveNum);
                    surfaceTreatmentRepository.updateDetailById(updateDetail);
                }
            });
        }

    }

    public void reworkReceive(MachineReceiveAddCmd addCmd) {
        List<MachineReceiveAddCmd.Part> reworkParts = addCmd.getParts().stream().filter(p -> p.getOperation() == BillOperation.RECEIVE_REWORKED_CREATE.getId()).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(reworkParts)) {
            for (MachineReceiveAddCmd.Part part : reworkParts) {
                List<MachineCheckDetail> checkDetails = checkRepository.selectReworkDetails(MachinePartScanQuery2.builder().requirementSerialNo(part.getRequirementSerialNo()).projectCode(part.getProjectCode()).partCode(part.getPartCode()).partVersion(part.getPartVersion()).build());
                // 返工单根据返工单号更新，如果没有返工单号则根据申请单和零件依次填充
                if (CollUtil.isEmpty(checkDetails)) {
                    throw new ServiceException(StrUtil.format("返工收货的零件，未找到质检返工单，申请单号：{}，项目号：{}，零件号/版本：{}/{}", part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                }
                if (part.getReworkSerialNo() != null) {
                    Optional<MachineCheckDetail> findCheck = checkDetails.stream().filter(c -> c.getSerialNo().equals(part.getReworkSerialNo())).findFirst();
                    if (findCheck.isEmpty()) {
                        throw new ServiceException(StrUtil.format("返工收货的零件，未找到指定的质检返工单，质检单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", part.getReworkSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    MachineCheckDetail checkDetail = findCheck.get();
                    long receiveNum = checkDetail.getReceivedNumber() == null ? part.getActualNumber() : checkDetail.getReceivedNumber() + part.getActualNumber();
                    if (checkDetail.getCheckedNumber() < receiveNum) {
                        throw new ServiceException(StrUtil.format("返工收货的零件，超出指定的质检返工单未收货数量，质检单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", part.getReworkSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    checkDetail.setReceiveTime(LocalDateTime.now());
                    checkDetail.setReceivedNumber(receiveNum);
                    checkRepository.updateDetailById(checkDetail);
                } else {
                    // 余量充足时，零件依次填充
                    long remainder = part.getActualNumber();
                    for (MachineCheckDetail checkDetail : checkDetails) {
                        if (remainder > 0) {
                            long receiveNum = checkDetail.getReceivedNumber() == null ? remainder : checkDetail.getReceivedNumber() + remainder;
                            if (checkDetail.getCheckedNumber() < receiveNum) {
                                remainder = receiveNum - checkDetail.getCheckedNumber();
                                receiveNum = checkDetail.getCheckedNumber();
                            } else {
                                remainder = 0;
                            }
                            checkDetail.setReceivedNumber(receiveNum);
                            checkDetail.setReceiveTime(LocalDateTime.now());
                            checkRepository.updateDetailById(checkDetail);
                        }
                    }
                    if (remainder > 0) {
                        throw new ServiceException(StrUtil.format("返工收货的零件，超出质检返工单未收货数量，超出{}个，申请单号：{}，项目号：{}，零件号/版本：{}/{}", remainder, part.getReworkSerialNo(), part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                }
            }
        }

    }

    public MachineReceive validAndAssembleReceive(MachineReceiveAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineReceive receive = receiveAssemble.toMachineReceive(addCmd);
        if (isCommit) {
            checkPart(receive, addCmd.isForceOperation());
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_receive").prefix("MRC" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            receive.setSerialNo(serialNoR.getSerialNo());
        }
        setApplyInfo(receive);
        receive.getParts().forEach(p -> {
            p.setSerialNo(receive.getSerialNo());
            p.setProvider(receive.getProvider());
        });
        return receive;
    }

    public void checkPart(MachineReceive receive, boolean forceOperation) {
        for (MachineReceiveDetail part : receive.getParts()) {
            // 收货出库仓库仓库
            BaseWarehouse outWarehouse = machineHelper.getReceiveOutWarehouse(part.getOperation());
            // 校验零件
            machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
            // 校验仓库
            machineHelper.existWarehouseByCode(part.getWarehouseCode());
            if (!forceOperation) {
                // 查询库存
                Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), outWarehouse.getCode());
                if (stockNumber < part.getActualNumber()) {
                    throw new ServiceException(StrUtil.format("待收货数量不足，请检查订单数量，项目号：{}，零件号/版本：{}/{}，待收货数量：{}，收货数量：{}",
                            part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getActualNumber()));
                }
            }
        }
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        receiveRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineReceiveResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine receive list params:{}", query);
        List<MachineReceive> list = receiveRepository.list(query);
        return receiveAssemble.toMachineReceiveRs(list);
    }

    @Override
    public MachineOrderPartR scan(MachineReceivePartScanQuery query) {
        log.info("scan part from machine receive params:{}", query);
        if (StrUtil.isBlank(query.getRequirementSerialNo()) && StrUtil.isBlank(query.getSerialNo())) {
            throw new ServiceException("机加工需求单号和订单号不能都为空");
        }
        // 校验收货仓库
        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
        if (warehouse.getType() == WarehouseType.BOARD.getType()) {
            machineHelper.usableBoard(warehouse, WarehouseStage.WAIT_CHECK.getId());
        }
        // 校验订单
        MachineOrderDetail orderDetail = orderRepository.selectPart(MachineOrderPartScanQuery.builder().serialNo(query.getSerialNo())
                .requirementSerialNo(query.getRequirementSerialNo()).projectCode(query.getProjectCode())
                .partCode(query.getPartCode()).partVersion(query.getPartVersion()).build());
        if (Objects.isNull(orderDetail)) {
            throw new ServiceException(MachineError.E200006, StrUtil.format("零件号/版本：{}/{}", query.getPartCode(), query.getPartVersion()));
        }
        MachineOrder order = orderRepository.selectBySerialNo(orderDetail.getSerialNo());
        if (order.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
            throw new ServiceException(MachineError.E200119, StrUtil.format("订单号：{}，零件号/版本：{}/{}", orderDetail.getSerialNo(), query.getPartCode(), query.getPartVersion()));
        }
        MachineOrderPartR machineOrderPartR = orderAssemble.toMachineOrderPartR(orderDetail);
        // 查询待收件库存
        BaseWarehouse outWarehouse = machineHelper.getReceiveOutWarehouse(query.getOperation());
        machineOrderPartR.setStockNumber(machineHelper.getStockNumber(orderDetail.getMaterialId(), outWarehouse.getCode()));
        return machineOrderPartR;
    }


    @Override
    public List<MachineOrderPartR> partChoose(MachineOrderPartListQuery query) {
        log.info("part choose query params:{}", query);
        return orderRepository.selectPartList(query);
    }

    @Async
    @Override
    public void importOrder(MachineReceiveImportCmd importCommand) {
        List<MachineReceive> receiveList = new ArrayList<>();
        Map<LocalDateTime, Map<String, List<MachineReceiveImportCmd.Part>>> groupByDateAndProvider = importCommand.getParts().stream().collect(Collectors.groupingBy(MachineReceiveImportCmd.Part::getReceiveTime, Collectors.groupingBy(MachineReceiveImportCmd.Part::getProvider)));
        groupByDateAndProvider.forEach((receiveTime, map) -> map.forEach((provider, list) -> {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_receive").prefix("MRC" + DateUtil.dateSerialStr(receiveTime)).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);

            MachineReceive receive = MachineReceive.builder().serialNo(serialNoR.getSerialNo()).receiveTime(receiveTime).provider(provider).build();
            receive.setStatus(ProcessStatus.COMMITTED);
            receive.setReceiverId(SecurityUtils.getLoginUser().getUser().getUserId());
            receive.setReceiver(SecurityUtils.getLoginUser().getUser().getNickName());
            receive.setReceiverNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
            receive.setParts(receiveAssemble.toMachineReceiveDetailsFromImport(list, serialNoR.getSerialNo()));
            receiveList.add(receive);
        }));
        receiveRepository.addReceiveBatch(receiveList);
    }

    @Override
    public List<MachineReceiveExportR> selectExportDataList(MachineOrderExportQuery query) {
        return receiveRepository.selectExportDataList(query);
    }

    @Override
    public SysFile print(String serialNo) {
        MachineReceive detail = receiveRepository.detail(serialNo);
        return machineHelper.receiveGenWord(detail);
    }

    @Override
    public List<MachineReceiveRecord> listRecord(MachineRecordQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(cn.hutool.core.date.DateUtil.endOfDay(query.getEndDate()));
        }
        return receiveRepository.listRecord(query);
    }

    @Override
    public List<MachineReceiveRecordExportR> exportRecord(MachineRecordQuery query) {
        return receiveAssemble.toMachineReceiveRecordERS(listRecord(query));
    }

    @Override
    public MachineReceiveResult detail(String serialNo) {
        log.info("query machine receive detail params:{}", serialNo);
        MachineReceive detail = receiveRepository.detail(serialNo);
        return receiveAssemble.toMachineReceiveR(detail);
    }

    @Override
    public void doStockWhenReceiveCommit(MachineReceive receive) {
        log.info("仓库收货，开始处理库存数据: {}", receive);
        // 收货单可能包含多个操作（正常收件、返工收件、表处收件）
        List<StockPrepareCmd> stockPrepareCmds = stockVoHelper.converterStockCmds(receive);
        for (StockPrepareCmd stockPrepareCmd : stockPrepareCmds) {
            stockService.doStock(stockPrepareCmd);
        }
        log.info("仓库收货，库存数据处理完成");
    }

    public void setApplyInfo(MachineReceive receive) {
        if (StrUtil.isEmpty(receive.getReceiver())) {
            receive.setReceiverId(SecurityUtils.getLoginUser().getUser().getUserId());
            receive.setReceiver(SecurityUtils.getLoginUser().getUser().getNickName());
            receive.setReceiverNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        }
        receive.setReceiveTime(LocalDateTime.now());
    }
}
