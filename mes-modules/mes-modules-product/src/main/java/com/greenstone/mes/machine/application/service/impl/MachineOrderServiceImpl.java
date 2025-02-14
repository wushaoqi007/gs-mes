package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.application.assemble.MachineOrderAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOrderImportCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOrderImportVO;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderContractExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderProgressQuery;
import com.greenstone.mes.machine.application.dto.result.*;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineOrderService;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.domain.entity.*;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.*;
import com.greenstone.mes.machine.infrastructure.mapper.MachineOrderMapper;
import com.greenstone.mes.machine.infrastructure.persistence.*;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.consts.BusinessKey;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.core.AbstractTableService;
import com.greenstone.mes.table.core.TableRepository;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2024-11-25-10:38
 */
@Slf4j
@TableFunction(id = "100000018", entityClass = MachineOrder.class, poClass = MachineOrderDO.class, updateReason = UpdateReason.NEVER)
@Service
public class MachineOrderServiceImpl extends AbstractTableService<MachineOrder, MachineOrderDO, MachineOrderMapper> implements MachineOrderService {

    private final MachineOrderAssemble orderAssemble;
    private final MachineProviderRepository providerRepository;
    private final MachineOrderRepository orderRepository;
    private final MachineHelper machineHelper;
    private final RemoteSystemService systemService;
    private final MachineRequirementRepository requirementRepository;
    private final MachineInquiryPriceRepository inquiryPriceRepository;
    private final MachineStockService stockService;
    private final MachineReceiveRepository receiveRepository;
    private final MachineCheckRepository checkRepository;
    private final MachineSurfaceTreatmentRepository surfaceTreatmentRepository;
    private final MachineWarehouseInRepository warehouseInRepository;
    private final MachineWarehouseOutRepository warehouseOutRepository;
    private final StockVoHelper stockVoHelper;

    public MachineOrderServiceImpl(TableRepository<MachineOrder, MachineOrderDO, MachineOrderMapper> tableRepository, ApplicationEventPublisher eventPublisher,
                                   MachineOrderAssemble orderAssemble, MachineProviderRepository providerRepository, MachineOrderRepository orderRepository,
                                   MachineHelper machineHelper, RemoteSystemService systemService, MachineRequirementRepository requirementRepository,
                                   MachineInquiryPriceRepository inquiryPriceRepository, MachineStockService stockService,
                                   MachineReceiveRepository receiveRepository, MachineCheckRepository checkRepository, MachineSurfaceTreatmentRepository surfaceTreatmentRepository,
                                   MachineWarehouseInRepository warehouseInRepository, MachineWarehouseOutRepository warehouseOutRepository,
                                   StockVoHelper stockVoHelper) {
        super(tableRepository, eventPublisher);
        this.orderAssemble = orderAssemble;
        this.providerRepository = providerRepository;
        this.orderRepository = orderRepository;
        this.machineHelper = machineHelper;
        this.systemService = systemService;
        this.requirementRepository = requirementRepository;
        this.inquiryPriceRepository = inquiryPriceRepository;
        this.stockService = stockService;
        this.receiveRepository = receiveRepository;
        this.checkRepository = checkRepository;
        this.surfaceTreatmentRepository = surfaceTreatmentRepository;
        this.warehouseInRepository = warehouseInRepository;
        this.warehouseOutRepository = warehouseOutRepository;
        this.stockVoHelper = stockVoHelper;
    }

    @Override
    public void importImpl(MultipartFile file, Map<String, Object> params) {
        log.info("Receive machine order import request");
        // 将表格转为VO
        List<MachineOrderImportVO> importVOs = new ExcelUtil<>(MachineOrderImportVO.class).toList(file);
        // 校验表格数据
        String validateResult = ValidationUtils.validate(importVOs);
        if (Objects.nonNull(validateResult)) {
            log.error(validateResult);
            throw new ServiceException(validateResult);
        }
        // 校验单价小数
        for (MachineOrderImportVO importVO : importVOs) {
            if (importVO.getUnitPrice() != null && importVO.getUnitPrice().scale() > 4) {
                String errMsg = StrUtil.format("零件 {} 的单价 {} 小数位不能超过4位", importVO.getPartCodeAndVersion(), importVO.getUnitPrice());
                throw new ServiceException(errMsg);
            }
        }

        log.info("开始导入，数据大小：{}", importVOs.size());
        MachineOrderImportCmd importCommand = MachineOrderImportCmd.builder().parts(orderAssemble.toPartImportCommands(importVOs)).build();
        List<MachineOrder> orderList = new ArrayList<>();
        // 按下图日期和供应商拆分订单
        Map<String, Map<LocalDate, List<MachineOrderImportCmd.Part>>> groupByProviderAndDate = importCommand.getParts().stream().collect(Collectors.groupingBy(MachineOrderImportCmd.Part::getProvider, Collectors.groupingBy(MachineOrderImportCmd.Part::getOrderTime)));
        groupByProviderAndDate.forEach((provider, map) -> {
            // 校验零件并组装订单
            map.forEach((orderTime, list) -> {
                MachineOrder order = MachineOrder.builder().provider(provider).orderTime(orderTime).build();
                order.setParts(orderAssemble.toMachineOrderDetailsFromImport(list));
                orderList.add(order);
            });
        });

        for (MachineOrder order : orderList) {
            create(order);
        }
    }

    @Override
    public String generateSerialNo(MachineOrder machineOrder) {
        if (StrUtil.isNotBlank(machineOrder.getSerialNo())) {
            if (orderRepository.isExist(machineOrder.getSerialNo()) != null) {
                throw new ServiceException("订单号已存在，不能使用此订单号");
            }
            return machineOrder.getSerialNo();
        } else {
            SerialNoR contractNoR = generateOrderSn(machineOrder.getProvider());
            while (orderRepository.isExist(contractNoR.getSerialNo()) != null) {
                log.info("订单号重复：{}，重新获取中", contractNoR.getSerialNo());
                contractNoR = generateOrderSn(machineOrder.getProvider());
            }
            return contractNoR.getSerialNo();
        }
    }

    @Override
    public void beforeCreate(MachineOrder order) {
        order.setContractNo(order.getSerialNo());
        if (CollUtil.isNotEmpty(order.getParts())) {
            order.getParts().forEach(d -> {
                d.setSerialNo(order.getSerialNo());
                d.setProvider(order.getProvider());
                d.setCreateBy(SecurityUtils.getUserId());
                d.setCreateTime(LocalDateTime.now());
                if (d.getUnitPrice() != null) {
                    BigDecimal totalPrice = BigDecimal.valueOf(d.getUnitPrice().doubleValue() * d.getProcessNumber());
                    d.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
                } else {
                    d.setUnitPrice(new BigDecimal(0));
                    d.setTotalPrice(new BigDecimal(0));
                }
            });
        }
        if (CollUtil.isNotEmpty(order.getAttachments())) {
            order.getAttachments().forEach(a -> {
                a.setSerialNo(order.getSerialNo());
                a.setCreateBy(SecurityUtils.getUserId());
                a.setCreateTime(LocalDateTime.now());
            });
        }

        machineHelper.orderPageValidate(order);
        machineHelper.orderGeneralValidate(order);
        validateByQuery(order.getParts());
    }

    @Override
    public void beforeUpdate(MachineOrder order) {
        // 更新订单号/合同号
        order.setContractNo(order.getSerialNo());
        MachineOrderDO exist = orderRepository.isExist(order.getSerialNo());
        if (exist != null && !Objects.equals(exist.getId(), order.getId())) {
            throw new ServiceException("订单号已存在，不能使用此订单号");
        }
        if (CollUtil.isNotEmpty(order.getParts())) {
            order.getParts().forEach(d -> {
                d.setSerialNo(order.getSerialNo());
                d.setProvider(order.getProvider());
                if (d.getUnitPrice() != null) {
                    if (d.getUnitPrice() != null && d.getUnitPrice().scale() > 4) {
                        String errMsg = StrUtil.format("零件 {}/{} 的单价 {} 小数位不能超过4位", d.getPartCode(), d.getPartVersion(), d.getUnitPrice());
                        throw new ServiceException(errMsg);
                    }
                    BigDecimal totalPrice = d.getUnitPrice().multiply(new BigDecimal(d.getProcessNumber()));
                    d.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
                } else {
                    d.setUnitPrice(new BigDecimal(0));
                    d.setTotalPrice(new BigDecimal(0));
                }
            });
        }

        if (CollUtil.isNotEmpty(order.getAttachments())) {
            order.getAttachments().forEach(a -> a.setSerialNo(order.getSerialNo()));
        }

        machineHelper.orderPageValidate(order);
        machineHelper.orderGeneralValidate(order);
//        validateByQuery(order.getParts());
    }

    @Override
    public void beforeSubmit(MachineOrder order) {
        if (CollUtil.isNotEmpty(order.getParts()) && !order.getSpecial()) {
            order.getParts().forEach(d -> {
                if (d.getUnitPrice() == null) {
                    throw new ServiceException(StrUtil.format("正常订单需填写单价，零件号/版本：{}/{}", d.getPartCode(), d.getPartVersion()));
                }
            });
        }
    }

    @Override
    public void updateApprovalChange(ApprovalChangeMsg msg) {
        super.updateApprovalChange(msg);
        if (BusinessKey.MACHINING_ORDER.equals(msg.getBusinessKey()) && msg.getStatus() == ProcessStatus.FINISH) {
            MachineOrder order = getEntity(msg.getItemId(), false);
            // 更新询价单状态
            Map<Optional<String>, List<MachineOrderDetail>> groupByInquiry = order.getParts().stream().collect(Collectors.groupingBy(a -> Optional.ofNullable(a.getInquiryPriceSerialNo())));
            groupByInquiry.forEach((inquirySerialNo, list) -> {
                if (inquirySerialNo.isPresent()) {
                    for (MachineOrderDetail part : list) {
                        inquiryPriceRepository.partOrdered(inquirySerialNo.get(), part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                    }
                    inquiryPriceRepository.updateHandleStatus(inquirySerialNo.get());
                }
            });
            // 订单新增后，待收件库存新增
            doStockWhenOrderCommit(order);
        }
    }

    @Override
    public void afterDelete(MachineOrder order) {
        log.info("删除订单后操作：还原待收件库存{}", order);
        // 已生效的订单仍然可以删除，但要还原库存等
        if (order.getDataStatus() == TableConst.DataStatus.EFFECTIVE) {
            machineHelper.revokeOrderStock(order.getParts());
        }
    }

    @Override
    public MultipartFile exportImpl(MachineOrder order) {
        List<MachineOrder> entities = getEntities(order);
        List<MachineOrderExportToImportR> results = new ArrayList<>();
        for (MachineOrder entity : entities) {
            for (MachineOrderDetail part : entity.getParts()) {
                MachineOrderExportToImportR exportData = orderAssemble.toExportBatchR(part);
                exportData.setOrderTime(entity.getOrderTime());
                exportData.setPartCodeAndVersion(part.getPartCode() + "/" + part.getPartVersion());
                results.add(exportData);
            }
        }
        return uploadFile(results);
    }

    @Override
    public List<MachineOrderProgressResult> selectOrderProgressList(MachineOrderProgressQuery query) {
        List<MachineOrderProgressResult> orderProgressResults = orderRepository.selectOrderDetailList(query);
        if (CollUtil.isNotEmpty(orderProgressResults)) {
            Set<String> orderSerialNos = orderProgressResults.stream().map(MachineOrderProgressResult::getSerialNo).collect(Collectors.toSet());
            // 收货单
            List<MachineReceiveDetailDO> receivedParts = getReceivedParts(orderSerialNos.stream().toList());
            // 检验单
            List<MachineCheckDetailDO> checkedParts = getCheckedParts(orderSerialNos.stream().toList());
            // 表处单
            List<MachineSurfaceTreatmentDetailDO> treatmentParts = getSurfaceTreatmentParts(orderSerialNos.stream().toList());
            // 入库单
            List<MachineWarehouseInDetailDO> inStockParts = getInStockParts(orderSerialNos.stream().toList());
            // 出库单
            List<MachineWarehouseOutDetailDO> outStockParts = getOutStockParts(orderSerialNos.stream().toList());
            for (MachineOrderProgressResult part : orderProgressResults) {
                // 收货
                List<MachineReceiveDetailDO> partReceiveList = receivedParts.stream().filter(a -> part.getSerialNo().equals(a.getOrderSerialNo())
                        && a.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                        && a.getProjectCode().equals(part.getProjectCode())
                        && a.getPartCode().equals(part.getPartCode())
                        && a.getPartVersion().equals(part.getPartVersion())).toList();
                // 质检
                List<MachineCheckDetailDO> partCheckedList = checkedParts.stream().filter(a -> part.getSerialNo().equals(a.getOrderSerialNo())
                        && a.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                        && a.getProjectCode().equals(part.getProjectCode())
                        && a.getPartCode().equals(part.getPartCode())
                        && a.getPartVersion().equals(part.getPartVersion())).toList();
                // 表处
                List<MachineSurfaceTreatmentDetailDO> partTreatmentList = treatmentParts.stream().filter(a -> part.getSerialNo().equals(a.getOrderSerialNo())
                        && a.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                        && a.getProjectCode().equals(part.getProjectCode())
                        && a.getPartCode().equals(part.getPartCode())
                        && a.getPartVersion().equals(part.getPartVersion())).toList();
                // 入库
                List<MachineWarehouseInDetailDO> partiInStockList = inStockParts.stream().filter(a -> part.getSerialNo().equals(a.getOrderSerialNo())
                        && a.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                        && a.getProjectCode().equals(part.getProjectCode())
                        && a.getPartCode().equals(part.getPartCode())
                        && a.getPartVersion().equals(part.getPartVersion())).toList();
                // 出库
                List<MachineWarehouseOutDetailDO> partOutStockList = outStockParts.stream().filter(a -> part.getSerialNo().equals(a.getOrderSerialNo())
                        && a.getRequirementSerialNo().equals(part.getRequirementSerialNo())
                        && a.getProjectCode().equals(part.getProjectCode())
                        && a.getPartCode().equals(part.getPartCode())
                        && a.getPartVersion().equals(part.getPartVersion())).toList();
                Long receiveNum = 0L;
                Long receiveNumST = 0L;
                Long receiveNumRw = 0L;
                if (CollUtil.isNotEmpty(partReceiveList)) {
                    for (MachineReceiveDetailDO machineReceiveDetailDO : partReceiveList) {
                        switch (machineReceiveDetailDO.getOperation()) {
                            // 正常收货
                            case 1 -> receiveNum += machineReceiveDetailDO.getActualNumber();
                            // 表处收货
                            case 7 -> receiveNumST += machineReceiveDetailDO.getActualNumber();
                            // 返工收货
                            case 9 -> receiveNumRw += machineReceiveDetailDO.getActualNumber();
                        }
                    }
                }
                Long checkedNumHG = 0L;
                Long checkedNumRW = 0L;
                Long checkedNumST = 0L;
                if (CollUtil.isNotEmpty(partCheckedList)) {
                    for (MachineCheckDetailDO machineCheckDetailDO : partCheckedList) {
                        switch (machineCheckDetailDO.getCheckResultType().getCode()) {
                            // 合格
                            case 1 -> checkedNumHG += machineCheckDetailDO.getCheckedNumber();
                            // 返工
                            case 2 -> checkedNumRW += machineCheckDetailDO.getCheckedNumber();
                            // 表处
                            case 3 -> checkedNumST += machineCheckDetailDO.getCheckedNumber();
                        }
                    }
                }
                //已收货=收货单收货数量（只算正常收货）
                part.setReceivedNumber(receiveNum);
                //已检验=质检单质检数量（只算合格的）
                part.setCheckedNumber(checkedNumHG);
                //已入库=入库单入库数量
                Long inStockNumber = partiInStockList.stream().mapToLong(MachineWarehouseInDetailDO::getInStockNumber).sum();
                part.setInStockNumber(inStockNumber);
                //已出库=出库单出库数量
                Long outStockNumber = partOutStockList.stream().mapToLong(MachineWarehouseOutDetailDO::getOutStockNumber).sum();
                part.setOutStockNumber(outStockNumber);
                // 待收件=订单数量-收货单收货数量（只算正常收货）
                part.setWaitReceivedNumber(Math.max(part.getProcessNumber() - receiveNum, 0));
                // 待质检=收货数量（正常+返工+表处）-质检单质检数量（合格+返工+表处）
                part.setWaitCheckedNumber(Math.max(receiveNum + receiveNumST + receiveNumRw - checkedNumHG - checkedNumRW - checkedNumST, 0));
                //返工中=质检单返工数量-返工收货数量
                part.setReworkingNumber(Math.max(checkedNumRW - receiveNumRw, 0));
                //待表处=质检单表处数量-表处单表处数量
                long stNum = partTreatmentList.stream().mapToLong(MachineSurfaceTreatmentDetailDO::getHandleNumber).sum();
                part.setWaitSurfaceTreatNumber(Math.max(checkedNumST - stNum, 0));
                // 表处中=表处单表处数量-表处收货数量
                part.setTreatingSurfaceNumber(Math.max(stNum - receiveNumST, 0));
                //待入库=收货数量（正常收货）-入库单入库数量
                part.setWaitInStockNumber(Math.max(receiveNum - inStockNumber, 0));
            }
        }
        return orderProgressResults;
    }

    @Override
    public List<MachineOrderProgressExportResult> selectOrderProgressExportList(MachineOrderProgressQuery query) {
        // 如果需要的话应该用easy excel 返回file
        return orderAssemble.toOrderProgressExportList(selectOrderProgressList(query));
    }

    @Override
    public List<MachineOrderExportR> selectExportDataList(MachineOrderExportQuery query) {
        return orderRepository.selectExportDataList(query);
    }

    @Override
    public synchronized SysFile contractPrint(MachineOrderContractExportQuery query) {
        MachineOrder order = orderRepository.getBySerialNo(query.getSerialNo());
        MachineOrderContractResult contractResult = orderAssemble.toMachineOrderContractResult(order);
        for (MachineOrderContractResult.MachineOrderContractDetail part : contractResult.getParts()) {
            BaseMaterial baseMaterial = machineHelper.checkMaterialById(part.getMaterialId());
            part.setUnit(baseMaterial.getUnit());
        }
        if (Objects.isNull(query.getPurchaseDate())) {
            query.setPurchaseDate(LocalDate.now());
        }
        if (StrUtil.isEmpty(query.getPurchaser())) {
            query.setPurchaser(SecurityUtils.getLoginUser().getUser().getNickName());
        }
        query.setContractNo(order.getContractNo());
        return machineHelper.orderContractGenExcel(contractResult, query);
    }

    public List<MachineReceiveDetailDO> getReceivedParts(List<String> serialNos) {
        List<MachineReceiveDetailDO> allParts = receiveRepository.selectDetailsByOrderSerialNos(serialNos);
        List<MachineReceiveDetailDO> submitParts = new ArrayList<>();
        if (CollUtil.isNotEmpty(allParts)) {
            Map<String, List<MachineReceiveDetailDO>> groupBySn = allParts.stream().collect(Collectors.groupingBy(MachineReceiveDetailDO::getSerialNo));
            List<MachineReceiveDO> submitDOs = receiveRepository.selectSubmitSerialNo(groupBySn.keySet().stream().toList());
            if (CollUtil.isNotEmpty(submitDOs)) {
                List<String> submitSerialNo = submitDOs.stream().map(MachineReceiveDO::getSerialNo).toList();
                for (String serialNo : submitSerialNo) {
                    submitParts.addAll(groupBySn.get(serialNo));
                }
            }
        }
        return submitParts;
    }

    public List<MachineCheckDetailDO> getCheckedParts(List<String> serialNos) {
        List<MachineCheckDetailDO> allParts = checkRepository.selectDetailsByOrderSerialNos(serialNos);
        List<MachineCheckDetailDO> submitParts = new ArrayList<>();
        if (CollUtil.isNotEmpty(allParts)) {
            Map<String, List<MachineCheckDetailDO>> groupBySn = allParts.stream().collect(Collectors.groupingBy(MachineCheckDetailDO::getSerialNo));
            List<MachineCheckDO> submitDOs = checkRepository.selectSubmitSerialNo(groupBySn.keySet().stream().toList());
            if (CollUtil.isNotEmpty(submitDOs)) {
                List<String> submitSerialNo = submitDOs.stream().map(MachineCheckDO::getSerialNo).toList();
                for (String serialNo : submitSerialNo) {
                    submitParts.addAll(groupBySn.get(serialNo));
                }
            }
        }
        return submitParts;
    }

    public List<MachineSurfaceTreatmentDetailDO> getSurfaceTreatmentParts(List<String> serialNos) {
        List<MachineSurfaceTreatmentDetailDO> allParts = surfaceTreatmentRepository.selectDetailsByOrderSerialNos(serialNos);
        List<MachineSurfaceTreatmentDetailDO> submitParts = new ArrayList<>();
        if (CollUtil.isNotEmpty(allParts)) {
            Map<String, List<MachineSurfaceTreatmentDetailDO>> groupBySn = allParts.stream().collect(Collectors.groupingBy(MachineSurfaceTreatmentDetailDO::getSerialNo));
            List<MachineSurfaceTreatmentDO> submitDOs = surfaceTreatmentRepository.selectSubmitSerialNo(groupBySn.keySet().stream().toList());
            if (CollUtil.isNotEmpty(submitDOs)) {
                List<String> submitSerialNo = submitDOs.stream().map(MachineSurfaceTreatmentDO::getSerialNo).toList();
                for (String serialNo : submitSerialNo) {
                    submitParts.addAll(groupBySn.get(serialNo));
                }
            }
        }
        return submitParts;
    }

    public List<MachineWarehouseInDetailDO> getInStockParts(List<String> serialNos) {
        List<MachineWarehouseInDetailDO> allParts = warehouseInRepository.selectDetailsByOrderSerialNos(serialNos);
        List<MachineWarehouseInDetailDO> submitParts = new ArrayList<>();
        if (CollUtil.isNotEmpty(allParts)) {
            Map<String, List<MachineWarehouseInDetailDO>> groupBySn = allParts.stream().collect(Collectors.groupingBy(MachineWarehouseInDetailDO::getSerialNo));
            List<MachineWarehouseInDO> submitDOs = warehouseInRepository.selectSubmitSerialNo(groupBySn.keySet().stream().toList());
            if (CollUtil.isNotEmpty(submitDOs)) {
                List<String> submitSerialNo = submitDOs.stream().map(MachineWarehouseInDO::getSerialNo).toList();
                for (String serialNo : submitSerialNo) {
                    submitParts.addAll(groupBySn.get(serialNo));
                }
            }
        }
        return submitParts;
    }

    public List<MachineWarehouseOutDetailDO> getOutStockParts(List<String> serialNos) {
        List<MachineWarehouseOutDetailDO> allParts = warehouseOutRepository.selectDetailsByOrderSerialNos(serialNos);
        List<MachineWarehouseOutDetailDO> submitParts = new ArrayList<>();
        if (CollUtil.isNotEmpty(allParts)) {
            Map<String, List<MachineWarehouseOutDetailDO>> groupBySn = allParts.stream().collect(Collectors.groupingBy(MachineWarehouseOutDetailDO::getSerialNo));
            List<MachineWarehouseOutDO> submitDOs = warehouseOutRepository.selectSubmitSerialNo(groupBySn.keySet().stream().toList());
            if (CollUtil.isNotEmpty(submitDOs)) {
                List<String> submitSerialNo = submitDOs.stream().map(MachineWarehouseOutDO::getSerialNo).toList();
                for (String serialNo : submitSerialNo) {
                    submitParts.addAll(groupBySn.get(serialNo));
                }
            }
        }
        return submitParts;
    }

    public void doStockWhenOrderCommit(MachineOrder order) {
        log.info("订单提交，开始处理库存数据: {}", order);
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(order);
        stockService.doStock(stockPrepareCmd);
        log.info("订单提交，处理库存数据完成");
    }

    public SerialNoR generateOrderSn(String providerName) {
        MachineProvider provider = providerRepository.findByName(providerName);
        SerialNoNextCmd nextCmd =
                SerialNoNextCmd.builder().type("contract_" + provider.getAbbrName()).prefix("GLSTZDH" + DateUtil.dateSerialStr(LocalDate.now())).build();
        return systemService.getNextCn(nextCmd);
    }

    public void validateByQuery(List<MachineOrderDetail> parts) {
        Map<String, List<MachineOrderDetail>> groupByRequirement = parts.stream().collect(Collectors.groupingBy(MachineOrderDetail::getRequirementSerialNo));
        groupByRequirement.forEach((requirementSerialNo, list) -> {
            List<MachineOrderDetail> orderDetailList = orderRepository.selectEffectiveParts(requirementSerialNo);
            // 申请单中零件是否存在
            MachineRequirement requirement = requirementRepository.getEffectiveRequirementBySerialNo(requirementSerialNo);
            if (requirement == null) {
                throw new ServiceException(StrUtil.format("未找到已生效的申请单：{}", requirementSerialNo));
            }
            for (MachineOrderDetail part : list) {
                if (part.getUnitPrice() != null && part.getUnitPrice().scale() > 4) {
                    String errMsg = StrUtil.format("零件 {}/{} 的单价 {} 小数位不能超过4位", part.getPartCode(), part.getPartVersion(), part.getUnitPrice());
                    throw new ServiceException(errMsg);
                }
                MachineRequirementDetail requirementDetail = machineHelper.existInMachineRequirement(requirement, part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                part.setMaterialId(requirementDetail.getMaterialId());
                // 校验订单零件是否重复
                if (CollUtil.isNotEmpty(orderDetailList)) {
                    Optional<MachineOrderDetail> findDuplicate = orderDetailList.stream().filter(o -> !o.getSerialNo().equals(part.getSerialNo()) && o.getPartCode().equals(part.getPartCode()) && o.getPartVersion().equals(part.getPartVersion())).findFirst();
                    if (findDuplicate.isPresent()) {
                        MachineOrderDetail duplicate = findDuplicate.get();
                        throw new ServiceException(StrUtil.format("请勿重复下单，在订单{}中，已包含零件/版本：{}/{}", duplicate.getSerialNo(), duplicate.getPartCode(), duplicate.getPartVersion()));
                    }
                }
            }
        });
    }

    public MockMultipartFile uploadFile(List<MachineOrderExportToImportR> results) {
        String fileName = "机加工订单" + System.currentTimeMillis();
        MockMultipartFile multipartFile;
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineOrderExportToImportR.class).sheet(fileName).doWrite(results);
            // 将输出流转为 multipartFile 并上传
            multipartFile = new MockMultipartFile("file", fileName + ".xlsx", null, outStream.toByteArray());
            outStream.close();
        } catch (IOException e) {
            log.error(fileName + "导出错误:" + e.getMessage());
            throw new RuntimeException(fileName + "导出错误");
        }
        return multipartFile;
    }
}
