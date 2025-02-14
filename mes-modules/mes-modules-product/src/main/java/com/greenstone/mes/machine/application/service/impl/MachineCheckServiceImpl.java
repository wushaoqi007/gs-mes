package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineCheckAssemble;
import com.greenstone.mes.machine.application.assemble.MachineOrderAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckResultCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartListQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineCheckPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineCheckService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.entity.MachineCheck;
import com.greenstone.mes.machine.domain.entity.MachineCheckDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrder;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineCheckRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineSurfaceTreatmentStageRepository;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.system.api.RemoteDictService;
import com.greenstone.mes.system.api.domain.SysDictData;
import com.greenstone.mes.system.api.domain.SysDictType;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
public class MachineCheckServiceImpl implements MachineCheckService {

    private final MachineCheckRepository checkRepository;
    private final MachineCheckAssemble checkAssemble;
    private final RemoteSystemService systemService;
    private final MachineStockManager stockManager;
    private final MachineSurfaceTreatmentStageRepository surfaceTreatmentStageRepository;
    private final MachineOrderOldRepository orderRepository;
    private final RemoteDictService dictService;
    private final MachineOrderAssemble orderAssemble;
    private final MachineHelper machineHelper;
    private final StockVoHelper stockVoHelper;
    private final MachineStockService stockService;

    @Transactional
    @Override
    public void saveDraft(MachineCheckAddCmd addCmd) {
        log.info("machine check save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheck check = validAndAssembleCheck(addCmd, isNew, false);
        check.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineCheckAddCmd addCmd) {
        log.info("machine check save commit params:{}", addCmd);
        // 校验订单
        List<MachineOrderDetail> orderDetailList = validAndOrder(addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheck check = validAndAssembleCheck(addCmd, isNew, true);
        check.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
        // 提交后操作:转移零件
        doStockWhenCheckCommit(check, addCmd.getOperation());
        // 返工的更新订单收货数量
        if (addCmd.getOperation() == BillOperation.CHECKED_NG_CREATE.getId()) {
            for (MachineOrderDetail orderDetail : orderDetailList) {
                orderRepository.update(orderDetail);
            }
        }
    }

    public List<MachineOrderDetail> validAndOrder(MachineCheckAddCmd addCmd) {
        List<MachineOrderDetail> updateOrderDetails = new ArrayList<>();
        Map<String, List<MachineCheckAddCmd.Part>> groupByOrderSerialNo = addCmd.getParts().stream().collect(Collectors.groupingBy(MachineCheckAddCmd.Part::getOrderSerialNo));
        groupByOrderSerialNo.forEach((orderSerialNo, list) -> {
            MachineOrder detail = orderRepository.detail(orderSerialNo);
            if (detail.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
                throw new ServiceException(StrUtil.format("订单未生效，订单号：{}", orderSerialNo));
            }
            for (MachineCheckAddCmd.Part part : list) {
                // 校验订单
                MachineOrderDetail updateOrderDetail = machineHelper.existInMachineOrder(detail, part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                if (addCmd.getOperation() == BillOperation.CHECKED_NG_CREATE.getId()) {
                    if (updateOrderDetail.getReceivedNumber() == null || updateOrderDetail.getReceivedNumber() == 0 || updateOrderDetail.getReceivedNumber() < part.getCheckedNumber()) {
                        throw new ServiceException(StrUtil.format("订单收货数量不足，无法返工，请检查订单，订单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", orderSerialNo, part.getRequirementSerialNo(), part.getProjectCode(), part.getPartCode(), part.getPartVersion()));
                    }
                    long receiveNumAfterRework = updateOrderDetail.getReceivedNumber() - part.getCheckedNumber();
                    updateOrderDetail.setReceivedNumber(receiveNumAfterRework);
                    updateOrderDetails.add(updateOrderDetail);
                }
                // 设置返工加工商，便于查询
                part.setProvider(updateOrderDetail.getProvider());
                part.setOrderDetailId(updateOrderDetail.getId().toString());
            }
        });
        return updateOrderDetails;
    }

    @Override
    public List<MachineCheckRecord> listRecord(MachineCheckPartListQuery query) {
        return checkRepository.listRecord(query);
    }

    @Override
    public List<MachineCheckRecord> reworkRecord(MachineRecordQuery query) {
        return checkRepository.reworkRecord(query);
    }

    @Override
    public List<MachineReworkRecordExportR> exportRecord(MachineRecordQuery query) {
        return checkAssemble.toMachineReworkRecordERS(checkRepository.reworkRecord(query));
    }

    @Override
    public SysFile print(String serialNo) {
        MachineCheck detail = checkRepository.detail(serialNo);
        return machineHelper.checkGenWord(detail);
    }

    public MachineCheck validAndAssembleCheck(MachineCheckAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineCheck check = checkAssemble.toMachineCheck(addCmd);
        BaseWarehouse inWarehouse;
        switch (BillOperation.getById(addCmd.getOperation())) {
            case CHECKED_OK_CREATE -> {
                inWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.CHECKED_OK);
                check.setCheckResultType(CheckResultType.QUALIFIED);
            }
            case CHECKED_TREAT_CREATE -> {
                inWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.WAIT_TREAT_SURFACE);
                check.setCheckResultType(CheckResultType.TREAT_SURFACE);
            }
            case CHECKED_NG_CREATE -> {
                inWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.REWORKING);
                check.setCheckResultType(CheckResultType.REWORK);
            }
            default -> throw new ServiceException(MachineError.E200302, addCmd.getOperation().toString());
        }
        if (isCommit) {
            checkPart(check, addCmd.isForceOperation());
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_check").prefix("MCK" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            check.setSerialNo(serialNoR.getSerialNo());
        }
        setApplyInfo(check);
        check.getParts().forEach(p -> {
            p.setSerialNo(check.getSerialNo());
            p.setCheckResultType(check.getCheckResultType());
            p.setInWarehouseCode(inWarehouse.getCode());
            p.setCheckDate(LocalDate.now());
            p.setCheckById(SecurityUtils.getLoginUser().getUser().getUserId());
            p.setCheckBy(SecurityUtils.getLoginUser().getUser().getNickName());
            p.setCheckByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        });
        return check;
    }

    public void checkPart(MachineCheck check, boolean forceOperation) {
        for (MachineCheckDetail part : check.getParts()) {
            if (check.getCheckResultType() == CheckResultType.REWORK && (StrUtil.isEmpty(part.getNgType()) || StrUtil.isEmpty(part.getSubNgType()))) {
                throw new ServiceException(MachineError.E200303);
            }
            // 校验零件
            machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
            // 校验仓库
            machineHelper.existWarehouseByCode(part.getOutWarehouseCode());
            if (!forceOperation) {
                // 查询库存
                Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), part.getOutWarehouseCode());
                if (stockNumber < part.getCheckedNumber()) {
                    throw new ServiceException(StrUtil.format("待质检数量不足，项目号：{}，零件号/版本：{}/{}，待质检数量：{}，质检数量：{}",
                            part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getCheckedNumber()));
                }
            }
        }
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        checkRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineCheckResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine check list params:{}", query);
        List<MachineCheck> list = checkRepository.list(query);
        return checkAssemble.toMachineCheckRs(list);
    }

    @Override
    public List<MachineCheckPartR> selectPartList(MachineCheckPartListQuery query) {
        return checkRepository.selectPartList(query);
    }

    @Override
    public List<MachineCheckCountR> checkCount(MachineCheckPartListQuery query) {
        List<MachineCheckCountR> checkCountRS = new ArrayList<>();
        if (Objects.isNull(query.getCheckDateStart()) || Objects.isNull(query.getCheckDateEnd())) {
            return checkCountRS;
        }
        List<MachineCheckPartR> machineCheckPartRS = checkRepository.selectPartList(query);
        if (CollUtil.isNotEmpty(machineCheckPartRS)) {
            Map<String, Map<LocalDate, List<MachineCheckPartR>>> groupByChecker = machineCheckPartRS.stream().collect(Collectors.groupingBy(MachineCheckPartR::getCheckBy, Collectors.groupingBy(MachineCheckPartR::getCheckDate)));
            groupByChecker.forEach((checker, map) -> map.forEach((checkDate, list) -> {
                long partTotal = list.stream().mapToLong(MachineCheckPartR::getCheckedNumber).sum();
                int paperTotal = list.stream().collect(Collectors.groupingBy(a -> a.getPartCode() + a.getPartVersion())).size();
                MachineCheckCountR result = MachineCheckCountR.builder().checkBy(checker).checkDate(checkDate).partTotal(partTotal).paperTotal(paperTotal).build();
                checkCountRS.add(result);
            }));
        }
        checkCountRS.sort(Comparator.comparing(MachineCheckCountR::getCheckDate));
        return checkCountRS;
    }

    @Override
    public MachineCheckPartR resultScan(MachineCheckPartScanQuery query) {
        log.info("machine check resultScan params:{}", query);
        if (StrUtil.isBlank(query.getRequirementSerialNo()) && StrUtil.isBlank(query.getOrderSerialNo())) {
            throw new ServiceException("机加工需求单号和订单号不能都为空");
        }
        BaseWarehouse warehouse;
        switch (BillOperation.getById(query.getOperation())) {
            case CHECKED_OK_CREATE -> {
                warehouse = machineHelper.existWarehouseNotBoard(query.getWarehouseCode());
                machineHelper.usableWarehouse(warehouse, WarehouseStage.CHECKED_OK);
            }
            case CHECKED_TREAT_CREATE -> {
                warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
                if (warehouse.getType() == WarehouseType.BOARD.getType()) {
                    machineHelper.usableBoard(warehouse, WarehouseStage.WAIT_TREAT_SURFACE.getId());
                } else {
                    machineHelper.usableWarehouse(warehouse, WarehouseStage.WAIT_TREAT_SURFACE);
                }
            }
            case CHECKED_NG_CREATE -> {
                warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
                if (warehouse.getType() == WarehouseType.BOARD.getType()) {
                    machineHelper.usableBoard(warehouse, WarehouseStage.WAIT_REWORKED.getId());
                } else {
                    machineHelper.usableWarehouse(warehouse, WarehouseStage.WAIT_REWORKED);
                }
            }
            default -> throw new ServiceException(MachineError.E200302, query.getOperation().toString());
        }
        MachineCheckPartR machineCheckPartR = checkRepository.selectPart(query);
        if (Objects.isNull(machineCheckPartR)) {
            throw new ServiceException(MachineError.E200304, StrUtil.format("质检单号：{}，零件号/版本：{}/{}", query.getCheckSerialNo(), query.getPartCode(), query.getPartVersion()));
        }
        // 查询质检中库存
        BaseWarehouse outWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.CHECKING);
        machineCheckPartR.setStockNumber(machineHelper.getStockNumber(machineCheckPartR.getMaterialId(), outWarehouse.getCode()));
        return machineCheckPartR;
    }

    @Override
    public MachineCheckResult detail(String serialNo) {
        log.info("query machine check detail params:{}", serialNo);
        MachineCheck detail = checkRepository.detail(serialNo);
        return checkAssemble.toMachineCheckR(detail);
    }


    public void doStockWhenCheckCommit(MachineCheck check, Integer operation) {
        log.info("质检，开始处理库存数据: {}", check);
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(check);
        stockService.doStock(stockPrepareCmd);
        log.info("质检，处理库存数据完成");
    }

    @Transactional
    @Override
    public void resultEntry(MachineCheckResultCmd resultCmd) {
        log.info("resultEntry params:{}", resultCmd);
        List<MachineCheckDetail> updateList = new ArrayList<>();
        // 质检中仓库
        BaseWarehouse outWarehouse = machineHelper.findWarehouseByStage(WarehouseStage.CHECKING);
        // 零件转移
        List<MachineStockTransferVo> stockTransferList = new ArrayList<>();
        MachineCheck machineCheck = checkRepository.detail(resultCmd.getCheckSerialNo());
        Map<String, List<MachineCheckResultCmd.Part>> groupByWarehouse = resultCmd.getParts().stream().collect(Collectors.groupingBy(MachineCheckResultCmd.Part::getInWarehouseCode));
        groupByWarehouse.forEach((warehouseCode, list) -> {
            BaseWarehouse warehouse;
            switch (BillOperation.getById(resultCmd.getOperation())) {
                case CHECKED_OK_CREATE -> warehouse = machineHelper.existWarehouseNotBoard(warehouseCode);
                case CHECKED_TREAT_CREATE ->
                        warehouse = machineHelper.getUsableWarehouseByCode(warehouseCode, WarehouseStage.WAIT_TREAT_SURFACE.getId());
                case CHECKED_NG_CREATE ->
                        warehouse = machineHelper.getUsableWarehouseByCode(warehouseCode, WarehouseStage.REWORKING.getId());
                default -> throw new ServiceException(MachineError.E200302, resultCmd.getOperation().toString());
            }
            List<MachineStockTransferVo.MaterialInfo> materialInfoList = new ArrayList<>();
            for (MachineCheckResultCmd.Part part : list) {
                Optional<MachineCheckDetail> find = machineCheck.getParts().stream().filter(p -> p.getId().equals(part.getCheckDetailId())).findFirst();
                if (find.isEmpty()) {
                    throw new ServiceException(MachineError.E200301, StrUtil.format("质检单号：{}，零件号/版本：{}/{}", machineCheck.getSerialNo(), part.getPartCode(), part.getPartVersion()));
                }
                MachineCheckDetail machineCheckDetail = find.get();
                // 查询库存
                Long stockNumber = machineHelper.getStockNumberWithProjectCode(machineCheckDetail.getProjectCode(), machineCheckDetail.getMaterialId(), outWarehouse.getCode());
                if (stockNumber < part.getCheckedNumber() && !resultCmd.isForceOperation()) {
                    throw new ServiceException(MachineError.E200014, StrUtil.format("项目代码：{}，零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                            machineCheckDetail.getProjectCode(), machineCheckDetail.getPartCode(), machineCheckDetail.getPartVersion(), stockNumber, part.getCheckedNumber()));
                }
                updateList.add(machineCheckDetail);
                setCheckInfo(part, machineCheckDetail);
                MachineStockTransferVo.MaterialInfo material = MachineStockTransferVo.MaterialInfo.builder()
                        .orderSerialNo(machineCheckDetail.getOrderSerialNo()).orderDetailId(machineCheckDetail.getOrderDetailId())
                        .projectCode(machineCheckDetail.getProjectCode()).materialId(machineCheckDetail.getMaterialId())
                        .number(part.getCheckedNumber()).build();
                materialInfoList.add(material);
                switch (BillOperation.getById(resultCmd.getOperation())) {
                    case CHECKED_OK_CREATE -> {
                        machineCheckDetail.setCheckResult(CheckResult.OK);
                        machineCheckDetail.setCheckResultType(CheckResultType.QUALIFIED);
                    }
                    case CHECKED_TREAT_CREATE -> {
                        machineCheckDetail.setCheckResult(CheckResult.NG);
                        machineCheckDetail.setCheckResultType(CheckResultType.TREAT_SURFACE);
//                        setSurfaceTreatmentStage(machineCheckDetail);
                    }
                    case CHECKED_NG_CREATE -> {
                        machineCheckDetail.setCheckResult(CheckResult.NG);
                        machineCheckDetail.setCheckResultType(CheckResultType.REWORK);
                        if (StrUtil.isEmpty(resultCmd.getNgType()) || StrUtil.isEmpty(resultCmd.getSubNgType())) {
                            throw new ServiceException(MachineError.E200303);
                        }
                        machineCheckDetail.setNgType(resultCmd.getNgType());
                        machineCheckDetail.setSubNgType(resultCmd.getSubNgType());
                    }
                    default -> throw new ServiceException(MachineError.E200302, resultCmd.getOperation().toString());
                }
            }

            MachineStockTransferVo stockTransferVo = MachineStockTransferVo.builder().operation(BillOperation.getById(resultCmd.getOperation())).inStockWhId(warehouse.getId()).remark("AUTO").sponsor(machineCheck.getCheckBy()).materialInfoList(materialInfoList).build();
            if (resultCmd.getOperation() == BillOperation.CHECKED_NG_CREATE.getId()) {
                MachineStockTransferVo.NgData ngData = MachineStockTransferVo.NgData.builder().ngType(resultCmd.getNgType()).subNgType(resultCmd.getSubNgType()).build();
                stockTransferVo.setNgData(ngData);
            }
            stockTransferList.add(stockTransferVo);
        });
        // 零件转移
        if (CollUtil.isNotEmpty(stockTransferList)) {
            for (MachineStockTransferVo stockTransferVo : stockTransferList) {
                log.info("check result transfer params:{}", stockTransferVo);
                stockManager.transfer(stockTransferVo);
            }
        }
        // 结果录入
        checkRepository.resultEntry(updateList);
        // 设置是否完成质检
        checkRepository.isFinished(resultCmd.getCheckSerialNo());
    }

    public void addDictData(String dictLabel) {
        SysDictType dictType = dictService.selectDictTypeByType("surface_treatment");
        if (Objects.isNull(dictType)) {
            throw new ServiceException(MachineError.E200307, "surface_treatment");
        }
        SysDictData surface_treatment = SysDictData.builder().dictType("surface_treatment").dictLabel(dictLabel).dictValue(UUID.randomUUID().toString()).build();
        List<SysDictData> dictData = dictService.getDictData("surface_treatment");
        if (CollUtil.isEmpty(dictData)) {
            // 新增表处字典类型
            dictService.addDictData(surface_treatment);
            return;
        }
        Optional<SysDictData> find = dictData.stream().filter(d -> d.getDictLabel().equals(dictLabel)).findFirst();
        if (find.isEmpty()) {
            // 新增表处字典类型
            dictService.addDictData(surface_treatment);
        }
    }

    public void setApplyInfo(MachineCheck check) {
        if (StrUtil.isEmpty(check.getCheckBy())) {
            check.setCheckById(SecurityUtils.getLoginUser().getUser().getUserId());
            check.setCheckBy(SecurityUtils.getLoginUser().getUser().getNickName());
            check.setCheckByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        }
        check.setCheckTime(LocalDateTime.now());
    }

    public void setCheckInfo(MachineCheckResultCmd.Part part, MachineCheckDetail machineCheckDetail) {
        machineCheckDetail.setToBeCheckedNumber(Math.max(machineCheckDetail.getToBeCheckedNumber() - part.getCheckedNumber(), 0));
        machineCheckDetail.setCheckedNumber(machineCheckDetail.getCheckedNumber() + part.getCheckedNumber());
        machineCheckDetail.setInWarehouseCode(part.getInWarehouseCode());
        machineCheckDetail.setCheckById(SecurityUtils.getLoginUser().getUser().getUserId());
        machineCheckDetail.setCheckBy(SecurityUtils.getLoginUser().getUser().getNickName());
        machineCheckDetail.setCheckByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        machineCheckDetail.setCheckDate(LocalDate.now());
    }
}
