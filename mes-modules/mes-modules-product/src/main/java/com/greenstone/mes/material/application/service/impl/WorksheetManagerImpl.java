package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.base.api.RemoteBomService;
import com.greenstone.mes.bom.request.BomEditByPartOrderReq;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.material.application.dto.PartCheckCmd;
import com.greenstone.mes.material.application.dto.WorkSheetCheckCountQuery;
import com.greenstone.mes.material.application.dto.WorkSheetPlaceOrderQuery;
import com.greenstone.mes.material.application.dto.result.WorksheetCheckCountR;
import com.greenstone.mes.material.application.dto.result.WorksheetPlaceOrderR;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import com.greenstone.mes.material.application.service.WorksheetManager;
import com.greenstone.mes.material.application.assembler.WorksheetAssembler;
import com.greenstone.mes.material.constant.PurchaseConstant;
import com.greenstone.mes.material.cqe.command.WorksheetImportCommand;
import com.greenstone.mes.material.cqe.command.WorksheetImportEditCommand;
import com.greenstone.mes.material.cqe.command.WorksheetSaveCommand;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.PurchaseOrderChange;
import com.greenstone.mes.material.domain.entity.ProcessOrder;
import com.greenstone.mes.material.domain.entity.WorksheetCheck;
import com.greenstone.mes.material.domain.entity.WorksheetPlaceOrder;
import com.greenstone.mes.material.dto.PartInGoodStockDto;
import com.greenstone.mes.material.dto.PartReceiveDto;
import com.greenstone.mes.material.infrastructure.enums.ProcessOrderStatus;
import com.greenstone.mes.material.enums.PurchasePartStatusCode;
import com.greenstone.mes.material.event.PartOrderConfirmEvent;
import com.greenstone.mes.material.event.data.ConfirmEventData;
import com.greenstone.mes.material.domain.repository.WorksheetRepository;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.response.PartBoardExportResp;
import com.greenstone.mes.material.response.PurchaseOrderDetailResp;
import com.greenstone.mes.material.response.PurchaseOrderExportResp;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.PurchaseOrderChangeService;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.domain.service.WorksheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class WorksheetManagerImpl implements WorksheetManager {

    @Autowired
    private WorksheetService worksheetService;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Autowired
    private RemoteBomService bomService;

    @Autowired
    private PurchaseOrderChangeService purchaseOrderChangeService;

    @Autowired
    private WorksheetAssembler worksheetAssembler;

    @Autowired
    private WorksheetRepository worksheetRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PartStageStatusManager partStageStatusManager;

    @Autowired
    private IBaseMaterialService materialService;


    @Override
    @Transactional
    public void purchaseOrderAdd(PurchaseOrderAddReq purchaseOrderAddReq) {
        List<PurchaseOrderAddReq.PurchaseOrderInfo> orderInfoList = purchaseOrderAddReq.getList();
        // 有加工单号的数据
        List<PurchaseOrderAddReq.PurchaseOrderInfo> hasOrderCodeList =
                orderInfoList.stream().filter(d -> StrUtil.isNotEmpty(d.getPartOrderCode())).collect(Collectors.toList());

        addPartOrderDetail(hasOrderCodeList.get(0).getProjectCode(), hasOrderCodeList, hasOrderCodeList.get(0).getPartOrderCode());
    }

    private void addPartOrderDetail(String projectCode, List<PurchaseOrderAddReq.PurchaseOrderInfo> orderCodeList, String orderCode) {
        ProcessOrderDO existOrder;
        List<ProcessOrderDetailDO> detailList = null;

        existOrder = worksheetService.getOneOnly(ProcessOrderDO.builder().code(orderCode).build());
        if (existOrder == null) {
            ProcessOrderDO orderAddEntity = ProcessOrderDO.builder().
                    code(orderCode).
                    projectCode(projectCode).
                    getNumber(0).
                    status(PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM).
                    isRework("否").
                    purchaseNumber(0L).companyType(orderCodeList.get(0).getCompanyType()).build();
            worksheetService.save(orderAddEntity);
            existOrder = orderAddEntity;
        } else {
            // 若加工单已废弃则不能上传
            if (PurchaseConstant.PurchaseOrderStatus.ABANDON.equals(existOrder.getStatus())) {
                log.error("Part order code must be not abandon");
                throw new ServiceException("加工单已废弃则不能上传：" + existOrder.getCode());
            }
            // 若加工单已确认则不能上传
            if (!PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM.equals(existOrder.getStatus())) {
                log.error("Part order code must be not confirmed");
                throw new ServiceException("加工单已确认不能上传：" + existOrder.getCode());
            }
            detailList = worksheetDetailService.list(new QueryWrapper<>(ProcessOrderDetailDO.builder().processOrderId(existOrder.getId()).build()));
        }

        // 总采购数量
        final int[] purchaseNumberTotal = {0};
        List<ProcessOrderDetailDO> orderDetailAddList = new ArrayList<>();
        List<ProcessOrderDetailDO> orderDetailUpdateList = new ArrayList<>();
        // 根据code_version分组：相同零件采购数量相加
        Map<String, List<PurchaseOrderAddReq.PurchaseOrderInfo>> collect =
                orderCodeList.stream().collect(Collectors.groupingBy(item -> item.getComponentCode() + "_" + item.getCode() + "_" + item.getVersion()));
        ProcessOrderDO finalExistOrder = existOrder;
        List<ProcessOrderDetailDO> finalDetailList = detailList;
        collect.forEach((codeVersion, list) -> {
            // 采购数量
            Long materialNumber = 0L;
            // edit by wsq 20220901 去除购买区分字段，都是正常新增
            for (PurchaseOrderAddReq.PurchaseOrderInfo purchaseOrderInfo : list) {
                materialNumber += purchaseOrderInfo.getMaterialNumber();
            }

            // 累计总采购数量
            purchaseNumberTotal[0] += materialNumber;
            ProcessOrderDetailDO existDetail = null;
            if (CollUtil.isNotEmpty(finalDetailList)) {
                existDetail = finalDetailList.stream().filter(d -> d.getCode().equals(list.get(0).getCode())).findFirst().orElse(null);
            }
            if (existDetail == null) {
                ProcessOrderDetailDO orderDetailAddEntity = ProcessOrderDetailDO.builder().
                        processOrderId(finalExistOrder.getId()).
                        projectCode(list.get(0).getProjectCode()).
                        componentCode(list.get(0).getComponentCode()).
                        componentName(list.get(0).getComponentName()).
                        code(list.get(0).getCode()).
                        version(list.get(0).getVersion()).
                        name(list.get(0).getName()).
                        originalNumber(materialNumber).
                        currentNumber(materialNumber).
                        paperNumber(list.get(0).getPaperNumber()).
                        surfaceTreatment(list.get(0).getSurfaceTreatment()).
                        rawMaterial(list.get(0).getRawMaterial()).
                        weight(list.get(0).getWeight()).
                        status(PurchaseConstant.PurchasePartStatus.TO_CONFIRM).
                        designer(list.get(0).getDesigner()).
                        getNumber(0L).
                        reason(list.get(0).getPurchaseReason()).
                        build();
                orderDetailAddList.add(orderDetailAddEntity);
            } else {
                ProcessOrderDetailDO orderDetailUpdateEntity = ProcessOrderDetailDO.builder().
                        id(existDetail.getId()).
                        currentNumber(existDetail.getCurrentNumber() + materialNumber).
                        originalNumber(existDetail.getOriginalNumber() + materialNumber).build();
                orderDetailUpdateList.add(orderDetailUpdateEntity);
            }


        });

        finalExistOrder.setPurchaseNumber((long) purchaseNumberTotal[0]);
        // 更新总购买数
        worksheetService.updateById(finalExistOrder);
        // 保存或更新加工单详情
        worksheetDetailService.saveBatch(orderDetailAddList);
        worksheetDetailService.updateBatchById(orderDetailUpdateList);
    }

    @Override
    @Transactional
    public Long addPartOrder(PartOrderAddReq partOrderAddReq) {
        log.debug("Add part order start");
        ProcessOrderDO partOrder = null;
        if (partOrderAddReq.getOrderId() != null) {
            log.info("Part order has id '{}'", partOrderAddReq.getOrderId());
            partOrder = worksheetService.getById(partOrderAddReq.getOrderId());
            if (partOrder == null) {
                log.info("Part order not exist, id '{}'", partOrderAddReq.getOrderId());
                throw new ServiceException(StrUtil.format("ID为'{}'的机加工单不存在", partOrderAddReq.getOrderId()));
            }
        } else if (StrUtil.isNotEmpty(partOrderAddReq.getOrderCode())) {
            log.info("Part order has code '{}'", partOrderAddReq.getOrderCode());
            ProcessOrderDO orderSelectEntity = ProcessOrderDO.builder().code(partOrderAddReq.getOrderCode()).build();
            partOrder = worksheetService.getOneOnly(orderSelectEntity);
            if (partOrder == null) {
                log.info("Part order with code {} is not exist.", partOrderAddReq.getOrderCode());
            }
        }
        if (partOrder != null && !PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM.equals(partOrder.getStatus())) {
            log.info("The part order has already confirmed: {}'", partOrder.getCode());
            throw new ServiceException(StrUtil.format("无法修改已确认的机加工单，编号'{}'", partOrder.getCode()));
        }

        long totalPartNum = partOrderAddReq.getOrderDetailList().stream().mapToLong(PartOrderAddReq.OrderDetail::getPartNumber).sum();
        if (partOrder == null) {
            String code = partOrderAddReq.getOrderCode() == null ? partOrderAddReq.getProjectCode() + "-" + System.currentTimeMillis() :
                    partOrderAddReq.getOrderCode();
            ProcessOrderDO orderAddEntity = ProcessOrderDO.builder().code(code).
                    projectCode(partOrderAddReq.getProjectCode()).companyType(partOrderAddReq.getOrderDetailList().get(0).getCompanyType()).
                    status(PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM).
                    purchaseNumber(totalPartNum).build();
            log.info("Add part order: {}", orderAddEntity);
            worksheetService.save(orderAddEntity);
            partOrder = orderAddEntity;
        } else {
            ProcessOrderDO orderUpdateEntity = ProcessOrderDO.builder().id(partOrder.getId()).
                    purchaseNumber(totalPartNum).build();
            worksheetService.updateById(orderUpdateEntity);
        }


        for (PartOrderAddReq.OrderDetail detail : partOrderAddReq.getOrderDetailList()) {
            ProcessOrderDetailDO detailSelectEntity = ProcessOrderDetailDO.builder().processOrderId(partOrder.getId()).
                    componentCode(detail.getComponentCode()).
                    code(detail.getPartCode()).
                    version(detail.getPartVersion()).build();
            ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(detailSelectEntity);
            if (existDetail == null) {
                ProcessOrderDetailDO detailAddEntity = ProcessOrderDetailDO.builder().processOrderId(partOrder.getId()).
                        projectCode(partOrderAddReq.getProjectCode()).
                        componentCode(detail.getComponentCode()).
                        componentName(detail.getComponentName()).
                        code(detail.getPartCode()).
                        name(detail.getPartName()).
                        version(detail.getPartVersion()).
                        originalNumber(detail.getPartNumber()).
                        currentNumber(detail.getPartNumber()).
                        paperNumber(detail.getPaperNumber()).
                        surfaceTreatment(detail.getSurfaceTreatment()).
                        rawMaterial(detail.getRawMaterial()).
                        weight(detail.getWeight()).
                        status(PurchaseConstant.PurchasePartStatus.TO_CONFIRM).
                        designer(detail.getDesigner()).reason(detail.getPurchaseReason()).build();
                log.info("Add part order detail: {}", detailAddEntity);
                worksheetDetailService.save(detailAddEntity);
            } else {
                ProcessOrderDetailDO detailUpdateEntity = ProcessOrderDetailDO.builder().id(existDetail.getId())
                        .processOrderId(partOrder.getId()).
                        projectCode(partOrderAddReq.getProjectCode()).
                        originalNumber(existDetail.getOriginalNumber() + detail.getPartNumber()).
                        currentNumber(existDetail.getCurrentNumber() + detail.getPartNumber()).
                        paperNumber(detail.getPaperNumber()).
                        surfaceTreatment(detail.getSurfaceTreatment()).
                        rawMaterial(detail.getRawMaterial()).
                        weight(detail.getWeight()).
                        designer(detail.getDesigner()).reason(detail.getPurchaseReason()).build();
                log.info("Update part order detail: {}", detailUpdateEntity);
                worksheetDetailService.updateById(detailUpdateEntity);
            }

        }

        return partOrder.getId();
    }

    @Override
    @Transactional
    public void confirmPurchaseOrder(PurchaseOrderConfirmReq purchaseOrderConfirmReq) {
        // 查找并更新采购单状态
        ProcessOrderDO processOrderDOWrapper = ProcessOrderDO.builder().id(purchaseOrderConfirmReq.getPurchaseOrderId()).build();
        ProcessOrderDO processOrderDO = worksheetService.getOneOnly(processOrderDOWrapper);
        if (Objects.isNull(processOrderDO)) {
            throw new ServiceException(StrUtil.format("机加工单不存在，id:{}", purchaseOrderConfirmReq.getPurchaseOrderId()));
        }
        if (processOrderDO.getStatus().equals(PurchaseConstant.PurchaseOrderStatus.CONFIRMED)) {
            throw new ServiceException(StrUtil.format("机加工单已确认，不能重复确认，id:{}", purchaseOrderConfirmReq.getPurchaseOrderId()));
        }
        processOrderDO.setStatus(PurchaseConstant.PurchaseOrderStatus.CONFIRMED);
        processOrderDO.setConfirmTime(new Date());
        processOrderDO.setConfirmBy(SecurityUtils.getLoginUser().getUser().getNickName());
        worksheetService.updateById(processOrderDO);
        // 更新采购单详情状态
        List<ProcessOrderDetailDO> processOrderDetailDOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(purchaseOrderConfirmReq.getList())) {
            for (PurchaseOrderConfirmReq.Confirm confirm : purchaseOrderConfirmReq.getList()) {
                // 校验比对结果
                if (confirm.getScanNumber() != confirm.getNumber().intValue()) {
                    throw new ServiceException(StrUtil.format("比对结果不正确：{}", confirm));
                }
                ProcessOrderDetailDO processOrderDetailDO = ProcessOrderDetailDO.builder().id(confirm.getId()).
                        scanNumber(confirm.getScanNumber()).
                        isFast(confirm.getIsFast()).
                        updateParts(confirm.getIsUpdateParts()).
                        repairParts(confirm.getIsRepairParts()).
                        isPurchase(confirm.getIsPurchase()).
                        provider(confirm.getProvider()).
                        processingTime(confirm.getProcessingTime()).
                        planTime(confirm.getPlanTime()).
                        type(confirm.getType()).
                        comparisonResult(confirm.getComparisonResult()).remark(confirm.getRemark()).
                        status(PurchaseConstant.PurchasePartStatus.TO_RECEIVED).
                        build();
                processOrderDetailDOS.add(processOrderDetailDO);
            }
            worksheetDetailService.updateBatchById(processOrderDetailDOS);
        }
        // 确认加工单后，在待收件区增加数据，并记录阶段状态
        partStageStatusManager.goToBeReceived(processOrderDO, processOrderDetailDOS);
        // 发布加工单确认事件
        eventPublisher.publishEvent(new PartOrderConfirmEvent(ConfirmEventData.builder().processOrderDO(processOrderDO).processOrderDetailDOList(processOrderDetailDOS).build()));
    }

    @Override
    public void updatePurchaseOrderDetail(PurchaseOrderConfirmReq purchaseOrderConfirmReq) {
        // 更新采购单详情状态
        List<ProcessOrderDetailDO> processOrderDetailDOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(purchaseOrderConfirmReq.getList())) {
            for (PurchaseOrderConfirmReq.Confirm confirm : purchaseOrderConfirmReq.getList()) {
                ProcessOrderDetailDO processOrderDetailDO = new ProcessOrderDetailDO();
                processOrderDetailDO.setId(confirm.getId());
                if (StrUtil.isNotBlank(confirm.getIsAbandon())) {
                    processOrderDetailDO.setStatus(PurchaseConstant.PurchasePartStatus.ABANDON);
                }
                if (StrUtil.isNotBlank(confirm.getRemark())) {
                    processOrderDetailDO.setRemark(confirm.getRemark());
                }
                processOrderDetailDOS.add(processOrderDetailDO);
            }
            worksheetDetailService.updateBatchById(processOrderDetailDOS);
        }

    }

    @Override
    public void abandonPurchaseOrderDetail(PurchaseOrderAbandonReq purchaseOrderAbandonReq) {
        // 更新采购单详情状态
        List<ProcessOrderDetailDO> processOrderDetailDOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(purchaseOrderAbandonReq.getList())) {
            for (PurchaseOrderAbandonReq.Confirm confirm : purchaseOrderAbandonReq.getList()) {
                ProcessOrderDetailDO processOrderDetailDO = new ProcessOrderDetailDO();
                processOrderDetailDO.setId(confirm.getId());
                if (StrUtil.isNotBlank(confirm.getIsAbandon())) {
                    processOrderDetailDO.setStatus(PurchaseConstant.PurchasePartStatus.ABANDON);
                }
                if (StrUtil.isNotBlank(confirm.getRemark())) {
                    processOrderDetailDO.setRemark(confirm.getRemark());
                }
                processOrderDetailDOS.add(processOrderDetailDO);
            }
            worksheetDetailService.updateBatchById(processOrderDetailDOS);
        }

    }

    @Override
    public void giveUpPurchaseOrder(PurchaseOrderEditReq purchaseOrderEditReq) {
        ProcessOrderDO processOrderDO =
                ProcessOrderDO.builder().id(purchaseOrderEditReq.getId()).remark(purchaseOrderEditReq.getRemark()).status(PurchaseConstant.PurchaseOrderStatus.ABANDON).build();
        ProcessOrderDetailDO build = ProcessOrderDetailDO.builder().processOrderId(processOrderDO.getId()).build();
        QueryWrapper<ProcessOrderDetailDO> queryWrapper = new QueryWrapper<>(build);
        List<ProcessOrderDetailDO> list = worksheetDetailService.list(queryWrapper);
        // 更新详情状态为废弃
        if (CollectionUtil.isNotEmpty(list)) {
            for (ProcessOrderDetailDO processOrderDetailDO : list) {
                processOrderDetailDO.setStatus(PurchaseConstant.PurchasePartStatus.ABANDON);
            }
            worksheetDetailService.updateBatchById(list);
        }
        // 更新
        worksheetService.updateById(processOrderDO);
    }

    @Override
    public List<PurchaseOrderExportResp> exportPurchaseOrder(Long id) {
        List<PurchaseOrderExportResp> purchaseOrderExportResList = new ArrayList<>();
        List<PurchaseOrderDetailResp> purchaseOrderDetailResp = worksheetDetailService.selectPurchaseOrderDetail(id);
        if (CollectionUtil.isNotEmpty(purchaseOrderDetailResp)) {
            for (PurchaseOrderDetailResp orderDetailResp : purchaseOrderDetailResp) {
                PurchaseOrderExportResp purchaseOrderExportResp = PurchaseOrderExportResp.builder().worksheetCode(orderDetailResp.getWorksheetCode()).
                        projectCode(orderDetailResp.getProjectCode()).
                        componentCode(orderDetailResp.getComponentCode()).
                        componentName(orderDetailResp.getComponentName()).
                        codeVersion(orderDetailResp.getCodeVersion()).
                        name(orderDetailResp.getName()).
                        materialNumber(orderDetailResp.getMaterialNumber()).
                        paperNumber(orderDetailResp.getPaperNumber()).
                        surfaceTreatment(orderDetailResp.getSurfaceTreatment()).
                        rawMaterial(orderDetailResp.getRawMaterial()).
                        weight(orderDetailResp.getWeight()).
                        getNumber(orderDetailResp.getGetNumber()).
                        isPurchase(orderDetailResp.getIsPurchase()).
                        isFast(orderDetailResp.getIsFast()).
                        provider(orderDetailResp.getProvider()).
                        status(PurchasePartStatusCode.getLabelByValue(orderDetailResp.getStatus())).
                        processingTime(orderDetailResp.getProcessingTime()).planTime(orderDetailResp.getPlanTime()).
                        type(orderDetailResp.getType()).build();
                purchaseOrderExportResList.add(purchaseOrderExportResp);
            }
        }
        return purchaseOrderExportResList;
    }

    @Override
    public List<PartBoardExportResp> listPartBoardExportData(PartsBoardListReq partsBoardListReq) {
        // 查找满足条件的零件
        List<PartBoardExportResp> partsBoardList = worksheetDetailService.selectPartsBoardList(partsBoardListReq);
        if (CollectionUtil.isNotEmpty(partsBoardList)) {
            for (PartBoardExportResp purchaseOrderDetail : partsBoardList) {
                // 收货超期：大于加工纳期的当天17点
                if (purchaseOrderDetail.getProcessingTime() != null) {
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDateTime dateTime = LocalDateTime.ofInstant(purchaseOrderDetail.getProcessingTime().toInstant(), zoneId);
                    dateTime = dateTime.withHour(17).withMinute(0).withSecond(0);
                    long processingTime = Date.from(dateTime.atZone(zoneId).toInstant()).getTime() / 1000;
                    // 标记收货超期
                    if (purchaseOrderDetail.getReceivingTime() != null) {
                        // 若收货日期大于加工纳期
                        if (purchaseOrderDetail.getProcessingTime() != null && processingTime < purchaseOrderDetail.getReceivingTime().getTime()) {
                            purchaseOrderDetail.setReceiveDelay("是");
                        }
                    } else if (purchaseOrderDetail.getProcessingTime() != null && processingTime < new Date().getTime()) {
                        // 加工纳期到了但是没有收货日期
                        purchaseOrderDetail.setReceiveDelay("是");
                    }
                }
                // 标记入库超期
                if (purchaseOrderDetail.getInStockTime() != null) {
                    // 若入库日期大于计划纳期
                    if (purchaseOrderDetail.getPlanTime() != null && purchaseOrderDetail.getPlanTime().getTime() < purchaseOrderDetail.getInStockTime().getTime()) {
                        purchaseOrderDetail.setInStockDelay("是");
                    }
                } else if (purchaseOrderDetail.getPlanTime() != null && purchaseOrderDetail.getPlanTime().getTime() < new Date().getTime()) {
                    // 计划纳期到了但是没有入库日期
                    purchaseOrderDetail.setInStockDelay("是");
                }

            }
        }
        return partsBoardList;
    }

    @Override
    public void partReceiveAction(PartReceiveDto partReceiveDto) {
        log.info("Part receive action start");
        ProcessOrderDO existOrder = null;
        for (PartReceiveDto.PartReceiveDetail receiveDetail : partReceiveDto.getPartReceiveDetailList()) {
            // 若没有加工单号，则忽略（老的条形码没有加工单号）
            if (receiveDetail.getPartOrderCode() == null) {
                log.warn("Receive part didn't have order code: {} {}", receiveDetail.getPartCode(), receiveDetail.getPartVersion());
                continue;
            }
            // 若找不到对应的加工单，也忽略
            ProcessOrderDO orderSelectEntity = ProcessOrderDO.builder().code(receiveDetail.getPartOrderCode()).build();
            if (existOrder == null) {
                existOrder = worksheetService.getOneOnly(orderSelectEntity);
            }
            if (existOrder == null) {
                log.warn("Part order is not exist: {}", receiveDetail.getPartOrderCode());
                continue;
            }

            ProcessOrderDetailDO detailSelectEntity = ProcessOrderDetailDO.builder().processOrderId(existOrder.getId()).
                    code(receiveDetail.getPartCode()).
                    version(receiveDetail.getPartVersion()).build();
            ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(detailSelectEntity);
            if (existDetail == null) {
                log.warn("Part order detail is not exist, orderCode:{}, partCode:{}, partVersion{}", receiveDetail.getPartOrderCode(),
                        receiveDetail.getPartCode(), receiveDetail.getPartVersion());
            } else {
                long totalReceiveNumber = existDetail.getGetNumber() + receiveDetail.getReceiveNumber();
                String status = totalReceiveNumber >= existDetail.getCurrentNumber() ? PurchaseConstant.PurchasePartStatus.RECEIVED :
                        PurchaseConstant.PurchasePartStatus.RECEIVING;
                ProcessOrderDetailDO detailUpdateEntity = ProcessOrderDetailDO.builder().id(existDetail.getId()).
                        getNumber(totalReceiveNumber).status(status).receivingTime(new Date()).build();
                log.info("Update part order");
                worksheetDetailService.updateById(detailUpdateEntity);
            }
        }

        if (existOrder != null && (
                PurchaseConstant.PurchaseOrderStatus.CONFIRMED.equals(existOrder.getStatus()) ||
                        PurchaseConstant.PurchaseOrderStatus.RECEIVING.equals(existOrder.getStatus()))) {
            ProcessOrderDetailDO detailSelectEntity = ProcessOrderDetailDO.builder().processOrderId(existOrder.getId()).build();
            List<ProcessOrderDetailDO> orderDetailList = worksheetDetailService.list(Wrappers.query(detailSelectEntity));
            boolean isAllReceived = true;
            for (ProcessOrderDetailDO detail : orderDetailList) {
                if (detail.getCurrentNumber() > detail.getGetNumber()) {
                    isAllReceived = false;
                    break;
                }
            }
            ProcessOrderDO orderUpdate = ProcessOrderDO.builder().id(existOrder.getId()).status(isAllReceived ?
                    PurchaseConstant.PurchaseOrderStatus.RECEIVED : PurchaseConstant.PurchaseOrderStatus.RECEIVING).build();
            worksheetService.updateById(orderUpdate);
        }
    }

    @Override
    public void partInGoodStockAction(PartInGoodStockDto partInGoodStockDto) {
        log.info("Part inGoodStock action start");
        ProcessOrderDO existOrder = null;
        for (PartInGoodStockDto.PartInGoodStockDetail inGoodStockDetail : partInGoodStockDto.getPartInGoodStockDetailList()) {
            if (inGoodStockDetail.getGoodNumber() == null) {
                log.warn("inGoodStock part didn't have inGoodStock number: {} {}:{}", inGoodStockDetail.getPartCode(),
                        inGoodStockDetail.getPartVersion(), inGoodStockDetail.getGoodNumber());
                continue;
            }
            // 若没有加工单号，则忽略（老的条形码没有加工单号）
            if (inGoodStockDetail.getPartOrderCode() == null) {
                log.warn("inGoodStock part didn't have order code: {} {}", inGoodStockDetail.getPartCode(), inGoodStockDetail.getPartVersion());
                continue;
            }
            // 若找不到对应的加工单，也忽略
            ProcessOrderDO orderSelectEntity = ProcessOrderDO.builder().code(inGoodStockDetail.getPartOrderCode()).build();
            if (existOrder == null) {
                existOrder = worksheetService.getOneOnly(orderSelectEntity);
            }
            if (existOrder == null) {
                log.warn("Part order is not exist: {}", inGoodStockDetail.getPartOrderCode());
                continue;
            }

            ProcessOrderDetailDO detailSelectEntity = ProcessOrderDetailDO.builder().processOrderId(existOrder.getId()).
                    code(inGoodStockDetail.getPartCode()).
                    version(inGoodStockDetail.getPartVersion()).build();
            ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(detailSelectEntity);
            if (existDetail == null) {
                log.warn("Part order detail is not exist, orderCode:{}, partCode:{}, partVersion{}", inGoodStockDetail.getPartOrderCode(),
                        inGoodStockDetail.getPartCode(), inGoodStockDetail.getPartVersion());
            } else {
                Integer totalGoodNum = existDetail.getGoodNum() == null ? 0 : existDetail.getGoodNum() + inGoodStockDetail.getGoodNumber().intValue();
                ProcessOrderDetailDO detailUpdateEntity = ProcessOrderDetailDO.builder().id(existDetail.getId()).
                        goodNum(totalGoodNum).inGoodStockTime(new Date()).build();
                log.info("Update part order");
                worksheetDetailService.updateById(detailUpdateEntity);
            }
        }
    }

    @Override
    @Transactional
    public void importWorksheet(@Valid WorksheetImportCommand importCommand) {
        log.info("Import process order, size: {}", importCommand.getPartImportCommands().size());
        importCommand.validate();
        // 将import命令转为save命令
        WorksheetSaveCommand worksheetSaveCommand = worksheetAssembler.toSaveCommand(importCommand);
        // 保存加工单
        saveWorksheet(worksheetSaveCommand);
    }

    @Override
    public void saveWorksheet(@Valid WorksheetSaveCommand saveCommand) {
        saveCommand.trim();
        // 将save命令转为entity
        ProcessOrder processOrder = worksheetAssembler.toProcessOrder(saveCommand);
        // 计算零件总数
        processOrder.calcTotalNumber();
        // 保存加工单
        worksheetRepository.saveProcessOrder(processOrder);
    }

    @Override
    @Transactional
    public void editPurchaseOrder(PartOrderEditReq partOrderEditReq) {
        // 查询机加工单
        ProcessOrderDO existOrder = worksheetService.getOneOnly(ProcessOrderDO.builder().id(partOrderEditReq.getId()).build());
        if (Objects.isNull(existOrder)) {
            throw new ServiceException("加工单不存在：" + partOrderEditReq.getOrderCode());
        }
        // 只有待确认的机加工单可以修改
        if (!PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM.equals(existOrder.getStatus())) {
            throw new ServiceException("加工单不是待确认状态，不可修改：" + partOrderEditReq.getOrderCode());
        }
        for (PartOrderEditReq.PartOrderInfo partOrderInfo : partOrderEditReq.getEditList()) {
            ProcessOrderDetailDO oneOnly = worksheetDetailService.getOneOnly(ProcessOrderDetailDO.builder().id(partOrderInfo.getDetailId()).build());
            if (Objects.isNull(oneOnly)) {
                throw new ServiceException("加工单详情不存在,详情id：" + partOrderInfo.getDetailId());
            }
            oneOnly.setCurrentNumber(partOrderInfo.getMaterialNumber());
            oneOnly.setPaperNumber(partOrderInfo.getPaperNumber());
            oneOnly.setRemark(partOrderInfo.getRemark());
            oneOnly.setRawMaterial(partOrderInfo.getRawMaterial());
            oneOnly.setSurfaceTreatment(partOrderInfo.getSurfaceTreatment());
            oneOnly.setWeight(partOrderInfo.getWeight());
            oneOnly.setDesigner(partOrderInfo.getDesigner());
            oneOnly.setReason(partOrderInfo.getPurchaseReason());
            worksheetDetailService.updateById(oneOnly);
        }
    }

    @Override
    @Transactional
    public void changeApplyPurchaseOrder(PurchaseOrderChangeApplyReq purchaseOrderChangeApplyReq) {
        // 查询机加工单
        ProcessOrderDO existOrder = worksheetService.getOneOnly(ProcessOrderDO.builder().id(purchaseOrderChangeApplyReq.getId()).build());
        if (Objects.isNull(existOrder)) {
            throw new ServiceException("加工单不存在,id：" + purchaseOrderChangeApplyReq.getId());
        }
        // 只有待确认的机加工单可以修改
        if (PurchaseConstant.PurchaseOrderStatus.TO_CONFIRM.equals(existOrder.getStatus())) {
            throw new ServiceException("加工单是待确认状态，无需变更申请，可直接修改,id：" + purchaseOrderChangeApplyReq.getId());
        }
        for (PurchaseOrderChangeApplyReq.PurchaseOrderInfo purchaseOrderInfo : purchaseOrderChangeApplyReq.getApplyList()) {
            ProcessOrderDetailDO oneOnly =
                    worksheetDetailService.getOneOnly(ProcessOrderDetailDO.builder().id(purchaseOrderInfo.getDetailId()).build());
            if (Objects.isNull(oneOnly)) {
                throw new ServiceException("加工单零件不存在,id：" + purchaseOrderChangeApplyReq.getId());
            }
            oneOnly.setApplyNumber(purchaseOrderInfo.getApplyNumber());
            oneOnly.setActualChangeNumber(null);
            oneOnly.setRemark(purchaseOrderInfo.getRemark());
            oneOnly.setApplyReason(purchaseOrderInfo.getApplyReason());
            oneOnly.setIsChanging("Y");
            worksheetDetailService.updateById(oneOnly);
            // 插入变更记录
            PurchaseOrderChange purchaseOrderChange = PurchaseOrderChange.builder()
                    .detailId(oneOnly.getId())
                    .code(oneOnly.getCode())
                    .version(oneOnly.getVersion())
                    .name(oneOnly.getName())
                    .partOrderCode(existOrder.getCode())
                    .originalNumber(oneOnly.getCurrentNumber())
                    .applyNumber(purchaseOrderInfo.getApplyNumber())
                    .applyReason(purchaseOrderInfo.getApplyReason())
                    .remark(purchaseOrderInfo.getRemark()).build();
            purchaseOrderChangeService.save(purchaseOrderChange);
        }
        existOrder.setIsChanging("Y");
        worksheetService.updateById(existOrder);
    }

    @Override
    @Transactional
    public void changeConfirmPurchaseOrder(PurchaseOrderChangeConfirmReq purchaseOrderChangeConfirmReq) {
        // 查询机加工单
        ProcessOrderDO existOrder = worksheetService.getOneOnly(ProcessOrderDO.builder().id(purchaseOrderChangeConfirmReq.getId()).build());
        if (Objects.isNull(existOrder)) {
            throw new ServiceException("加工单不存在,id：" + purchaseOrderChangeConfirmReq.getId());
        }
        if (!"Y".equals(existOrder.getIsChanging())) {
            throw new ServiceException("加工单未变更，不可确认,id：" + purchaseOrderChangeConfirmReq.getId());
        }
        // 待修改的bom
        List<BomEditByPartOrderReq> bomEditByPartOrderReqList = new ArrayList<>();
        for (PurchaseOrderChangeConfirmReq.PurchaseOrderInfo purchaseOrderInfo : purchaseOrderChangeConfirmReq.getConfirmList()) {
            ProcessOrderDetailDO oneOnly =
                    worksheetDetailService.getOneOnly(ProcessOrderDetailDO.builder().id(purchaseOrderInfo.getDetailId()).build());
            if (Objects.isNull(oneOnly)) {
                throw new ServiceException("加工单零件不存在,id：" + purchaseOrderChangeConfirmReq.getId());
            }
            // 插入变更记录
            PurchaseOrderChange purchaseOrderChange = PurchaseOrderChange.builder()
                    .detailId(oneOnly.getId())
                    .code(oneOnly.getCode())
                    .version(oneOnly.getVersion())
                    .name(oneOnly.getName())
                    .partOrderCode(existOrder.getCode())
                    .originalNumber(oneOnly.getCurrentNumber())
                    .applyNumber(oneOnly.getApplyNumber())
                    .actualChangeNumber(purchaseOrderInfo.getActualChangeNumber())
                    .remark(purchaseOrderInfo.getRemark()).build();
            purchaseOrderChangeService.save(purchaseOrderChange);
            // 更新机加工单详情
            oneOnly.setActualChangeNumber(purchaseOrderInfo.getActualChangeNumber());
            oneOnly.setCurrentNumber(purchaseOrderInfo.getActualChangeNumber());
            oneOnly.setRemark(purchaseOrderInfo.getRemark());
            oneOnly.setIsChanging("N");
            worksheetDetailService.updateById(oneOnly);
            // 根据变更原因更新bom1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他
            if (Integer.parseInt(oneOnly.getApplyReason()) <= 3) {
                BomEditByPartOrderReq bomEditByPartOrderReq = BomEditByPartOrderReq.builder()
                        .componentCode(oneOnly.getComponentCode())
                        .materialCode(oneOnly.getCode())
                        .materialVersion(oneOnly.getVersion())
                        .materialNumber(oneOnly.getCurrentNumber()).build();
                bomEditByPartOrderReqList.add(bomEditByPartOrderReq);
            }
        }
        existOrder.setIsChanging("N");
        worksheetService.updateById(existOrder);
        // 更新bom
        if (CollUtil.isNotEmpty(bomEditByPartOrderReqList)) {
            bomService.updateBomByPartOrder(bomEditByPartOrderReqList);
        }
    }

    @Override
    public List<ProcessOrderDetailDO> getChangeDetail(Long orderId) {
        QueryWrapper<ProcessOrderDetailDO> queryWrapper =
                Wrappers.query(ProcessOrderDetailDO.builder().processOrderId(orderId).isChanging("Y").build());
        return worksheetDetailService.list(queryWrapper);
    }

    @Override
    @Transactional
    public void updatePartOrderInfo(List<PartOrderInfoEdit> partOrderInfoEditList) {
        for (PartOrderInfoEdit partOrderInfoEdit : partOrderInfoEditList) {
            QueryWrapper<ProcessOrderDO> purchaseOrderQueryWrapper =
                    Wrappers.query(ProcessOrderDO.builder().code(partOrderInfoEdit.getPartOrderCode()).build());
            ProcessOrderDO processOrderDO = worksheetService.getOneOnly(purchaseOrderQueryWrapper);
            if (Objects.isNull(processOrderDO)) {
                log.error("机加工单不存在,id：" + partOrderInfoEdit.getPartOrderCode());
                throw new ServiceException("机加工单不存在,id：" + partOrderInfoEdit.getPartOrderCode());
            }
            QueryWrapper<ProcessOrderDetailDO> queryWrapper = Wrappers.query(ProcessOrderDetailDO.builder()
                    .processOrderId(processOrderDO.getId())
                    .projectCode(partOrderInfoEdit.getProjectCode())
                    .code(partOrderInfoEdit.getPartCode())
                    .version(partOrderInfoEdit.getPartVersion()).build());
            ProcessOrderDetailDO processOrderDetailDO = worksheetDetailService.getOneOnly(queryWrapper);
            if (Objects.isNull(processOrderDetailDO)) {
                log.error("机加工单中不存在该零件,加工单号：" + partOrderInfoEdit.getPartOrderCode() + ",零件号/版本：" + partOrderInfoEdit.getPartCode() + "/" + partOrderInfoEdit.getPartVersion());
                throw new ServiceException("机加工单中不存在该零件,加工单号：" + partOrderInfoEdit.getPartOrderCode() + ",零件号/版本：" + partOrderInfoEdit.getPartCode() + "/" + partOrderInfoEdit.getPartVersion());
            }
            processOrderDetailDO.setType(partOrderInfoEdit.getType());
            processOrderDetailDO.setIsFast(partOrderInfoEdit.getIsFast());
            processOrderDetailDO.setProvider(partOrderInfoEdit.getProvider());
            processOrderDetailDO.setProcessingTime(partOrderInfoEdit.getProcessingTime());
            processOrderDetailDO.setPlanTime(partOrderInfoEdit.getPlanTime());
            // 更新
            worksheetDetailService.updateById(processOrderDetailDO);
        }
    }

    @Override
    @Transactional
    public void importEditWorksheet(@Valid WorksheetImportEditCommand importEditCommand) {
        log.info("Import worksheet edit, size: {}", importEditCommand.getPartImportEditCommands().size());
        importEditCommand.validate();
        // 更新加工单
        worksheetRepository.updateWorksheet(importEditCommand);
    }

    @Override
    @Transactional
    public void removeWorksheetById(Long id) {
        ProcessOrderDO existDO = worksheetService.getById(id);
        if (Objects.isNull(existDO)) {
            throw new ServiceException(BizError.E25001, StrUtil.format("id:{}", id));
        }
        if (Integer.parseInt(existDO.getStatus()) != ProcessOrderStatus.TO_CONFIRM.getStatus()) {
            throw new ServiceException(BizError.E25010, existDO.getCode());
        }
        // 删除加工单及详情
        worksheetDetailService.remove(Wrappers.query(ProcessOrderDetailDO.builder().processOrderId(id).build()));
        worksheetService.removeById(id);
    }

    @Override
    public List<WorksheetPlaceOrderR> selectWorksheetPlaceOrderList(WorkSheetPlaceOrderQuery placeOrderQuery) {
        List<WorksheetPlaceOrder> worksheetPlaceOrderList = worksheetDetailService.selectWorksheetPlaceOrderList(placeOrderQuery);
        return worksheetAssembler.toWorksheetPlaceOrderRs(worksheetPlaceOrderList);
    }

    @Override
    public List<WorksheetCheckCountR> selectWorksheetCheckCountList(WorkSheetCheckCountQuery checkCountQuery) {
        List<WorksheetCheck> worksheetCheckList = worksheetDetailService.selectWorksheetCheckList(checkCountQuery);
        return worksheetAssembler.toWorksheetCheckCountRs(worksheetCheckList);
    }

    @Override
    public ProcessOrderDetailDO checkPart(PartCheckCmd partCheckCmd) {
        log.info("part check params:{}", partCheckCmd);
        // 检验加工单是否存在
        ProcessOrderDO processOrderDO = worksheetService.selectByCode(partCheckCmd.getWorksheetCode());
        if (Objects.isNull(processOrderDO)) {
            throw new ServiceException(BizError.E25001);
        }
        // 检验物料是否存在
        BaseMaterial material = materialService.getOneOnly(BaseMaterial.builder().code(partCheckCmd.getPartCode()).version(partCheckCmd.getPartVersion()).build());
        if (Objects.isNull(material)) {
            throw new ServiceException(BizError.E20001);
        }
        log.info("part check material find:{}", material);
        // 检验加工单零件是否存在
        ProcessOrderDetailDO selectDetailDO = ProcessOrderDetailDO.builder().materialId(material.getId()).
                processOrderId(processOrderDO.getId()).
                componentCode(partCheckCmd.getComponentCode()).
                code(partCheckCmd.getPartCode()).
                version(partCheckCmd.getPartVersion()).build();
        ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(selectDetailDO);
        if (Objects.isNull(existDetail)) {
            throw new ServiceException(BizError.E25009);
        }
        return existDetail;
    }

}
