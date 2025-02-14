package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.util.concurrent.AtomicDouble;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.PartProgressQuery;
import com.greenstone.mes.material.application.dto.StockTransferVo;
import com.greenstone.mes.material.application.dto.result.PartProgressR;
import com.greenstone.mes.material.application.dto.result.ProjectProgressR;
import com.greenstone.mes.material.application.assembler.PartStageStatusAssembler;
import com.greenstone.mes.material.domain.*;
import com.greenstone.mes.material.domain.service.*;
import com.greenstone.mes.material.enums.PartProgressType;
import com.greenstone.mes.material.enums.ProjectProgressType;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.material.infrastructure.enums.PartStep;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.application.service.MaterialStockManager;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.response.MaterialWorksheetProgressListResp;
import com.greenstone.mes.material.response.MaterialWorksheetProgressStatResp;
import com.greenstone.mes.material.response.PartReworkStatResp;
import com.greenstone.mes.material.response.PartStageStatusListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 零件阶段状态接口实现
 *
 * @author wushaoqi
 * @date 2022-12-13-15:10
 */
@Slf4j
@Service
public class PartStageStatusManagerImpl implements PartStageStatusManager {

    @Autowired
    private WorksheetService worksheetService;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Autowired
    private IBaseWarehouseService warehouseService;

    @Autowired
    private IBaseMaterialService materialService;

    @Autowired
    private PartStageStatusService partStageStatusService;

    @Autowired
    private MaterialStockManager stockManager;

    @Autowired
    private PartStageStatusAssembler partStageStatusAssembler;

    @Autowired
    private MaterialStockService stockService;

    @Override
    public void savePartStageStatus(StockOperationEventData operationEventData) {
        log.info("start part stage status save");
        BaseWarehouse warehouse = operationEventData.getWarehouse();

        // 校验零件
        List<StockOperationEventData.StockMaterial> materialList = operationEventData.getMaterialList();
        if (CollUtil.isEmpty(materialList)) {
            log.warn("part stage status save failed, material list is empty");
            return;
        }

        // 是否从良品库之外的仓库直接领用
        boolean useWithUnfinished = operationEventData.getAction() == StockAction.OUT &&
                operationEventData.getOperation() == BillOperation.USED_CREATE && warehouse.getStage() != WarehouseStage.GOOD.getId();

        List<PartStageStatus> addList = new ArrayList<>();
        List<PartStageStatus> updateList = new ArrayList<>();
        for (StockOperationEventData.StockMaterial stockMaterial : materialList) {
            // 查询加工单
            QueryWrapper<ProcessOrderDO> orderDOQueryWrapper =
                    Wrappers.query(ProcessOrderDO.builder().code(stockMaterial.getWorksheetCode()).build());
            ProcessOrderDO processOrderDO = worksheetService.getOneOnly(orderDOQueryWrapper);
            if (Objects.isNull(processOrderDO)) {
                log.warn("part stage status save failed ,worksheet not found,worksheet code:{}", stockMaterial.getWorksheetCode());
                continue;
            }
            // 查询加工单详情
            BaseMaterial material = materialService.getById(stockMaterial.getMaterial().getId());
            QueryWrapper<ProcessOrderDetailDO> orderDetailDOQueryWrapper = Wrappers.query(ProcessOrderDetailDO.builder()
                    .processOrderId(processOrderDO.getId()).componentCode(stockMaterial.getComponentCode())
                    .code(material.getCode()).version(material.getVersion()).build());
            ProcessOrderDetailDO processOrderDetailDO = worksheetDetailService.getOneOnly(orderDetailDOQueryWrapper);
            if (Objects.isNull(processOrderDetailDO)) {
                log.warn("part stage status save failed ,worksheet detail not found,worksheet code:{},component code:{},code:{},version:{}",
                        stockMaterial.getWorksheetCode(), stockMaterial.getComponentCode(), material.getCode(), material.getVersion());
                continue;
            }
            // 记录零件阶段状态
            PartStageStatus query = PartStageStatus.builder().stage(warehouse.getStage()).worksheetDetailId(processOrderDetailDO.getId()).build();
            PartStageStatus partStageStatus = partStageStatusService.getOneOnly(query);
            if (Objects.isNull(partStageStatus)) {
                // 查询物料并关联，方便后续查询库存及仓库位置
                BaseMaterial queryMaterial = materialService.queryBaseMaterial(BaseMaterial.builder().code(processOrderDetailDO.getCode()).version(processOrderDetailDO.getVersion()).build());
                if (Objects.isNull(queryMaterial)) {
                    log.warn("part stage status save failed,material not found, code: {} version: {} ", processOrderDetailDO.getCode(),
                            processOrderDetailDO.getVersion());
                    continue;
                }
                partStageStatus = newPartStageStatus(warehouse, queryMaterial, processOrderDO, processOrderDetailDO, stockMaterial,
                        operationEventData.getAction().getId());
                addList.add(partStageStatus);
            } else {
                updateList.add(updatePartStageStatus(partStageStatus, stockMaterial, operationEventData.getAction().getId()));
            }

            if (useWithUnfinished) {
                // 记录零件阶段状态
                PartStageStatus query2 =
                        PartStageStatus.builder().stage(WarehouseStage.GOOD.getId()).worksheetDetailId(processOrderDetailDO.getId()).build();
                PartStageStatus partStageStatus2 = partStageStatusService.getOneOnly(query2);
                if (Objects.isNull(partStageStatus2)) {
                    // 查询物料并关联，方便后续查询库存及仓库位置
                    BaseMaterial queryMaterial = materialService.queryBaseMaterial(BaseMaterial.builder().code(processOrderDetailDO.getCode()).version(processOrderDetailDO.getVersion()).build());
                    if (Objects.isNull(queryMaterial)) {
                        log.warn("part stage status save failed,material not found, code: {} version: {} ", processOrderDetailDO.getCode(),
                                processOrderDetailDO.getVersion());
                        continue;
                    }
                    BaseWarehouse finishWh = BaseWarehouse.builder().stage(WarehouseStage.GOOD.getId()).build();
                    partStageStatus2 = newPartStageStatus(finishWh, queryMaterial, processOrderDO, processOrderDetailDO, stockMaterial,
                            StockAction.OUT.getId());
                    addList.add(partStageStatus2);
                } else {
                    updateList.add(updatePartStageStatusUseNotWithFinish(partStageStatus2, stockMaterial));
                }
            }
        }
        partStageStatusService.saveBatch(addList);
        partStageStatusService.updateBatchById(updateList);


    }

    @Override
    public void goToBeReceived(ProcessOrderDO processOrder, List<ProcessOrderDetailDO> processOrderDetailDOList) {
        if (CollUtil.isNotEmpty(processOrderDetailDOList)) {
            // 待收件仓库查询
            BaseWarehouse inStockWarehouse = warehouseService.findOnlyOneByStage(WarehouseStage.WAIT_RECEIVE.getId());
            if (Objects.isNull(inStockWarehouse)) {
                log.warn("warehouse not found when confirm to be received,warehouse stage:{}", WarehouseStage.WAIT_RECEIVE.getId());
                return;
            }

            // 确认加工单后，更新出入库数据
            stockManager.transfer(confirmTransferVo(inStockWarehouse.getId(), processOrder, processOrderDetailDOList));
        }
    }

    /**
     * 出入库数据构建
     */
    public StockTransferVo confirmTransferVo(Long warehouseId, ProcessOrderDO processOrder, List<ProcessOrderDetailDO> processOrderDetailDOList) {
        List<StockTransferVo.MaterialInfo> materialInfoList = new ArrayList<>();
        for (ProcessOrderDetailDO detailDO : processOrderDetailDOList) {
            detailDO = worksheetDetailService.getById(detailDO.getId());
            StockTransferVo.MaterialInfo material = StockTransferVo.MaterialInfo.builder().worksheetCode(processOrder.getCode())
                    .projectCode(processOrder.getProjectCode()).componentCode(detailDO.getComponentCode()).materialId(detailDO.getMaterialId())
                    .number(detailDO.getCurrentNumber()).build();
            materialInfoList.add(material);
        }
        return StockTransferVo.builder().operation(BillOperation.ORDER_CREATE).inStockWhId(warehouseId).remark("AUTO").sponsor("admin").operateAll(false).materialInfoList(materialInfoList).build();
    }

    @Override
    public List<PartReworkStatResp> reworkStat(PartsReworkStatReq partsReworkStatReq) {
        log.info("start stat rework");
        // 获取所有加工商及加工量
        List<PartReworkStatResp> partProviderStat = partStageStatusService.partProviderStat(partsReworkStatReq);
        // 获取返工的加工商及返工量
        List<PartReworkStatResp> partReworkStat = partStageStatusService.partReworkStat(partsReworkStatReq);
        // 计算返工率：返工率=返工量/加工量
        if (CollUtil.isNotEmpty(partProviderStat)) {
            DecimalFormat decimalFormat = new DecimalFormat("0.##%");
            for (PartReworkStatResp provider : partProviderStat) {
                Optional<PartReworkStatResp> reworkProvider =
                        partReworkStat.stream().filter(p -> p.getProvider().equals(provider.getProvider())).findFirst();
                if (reworkProvider.isPresent()) {
                    // 返工率=返工量/加工量
                    String reworkRate = decimalFormat.format((double) reworkProvider.get().getReworkTotal() / (double) provider.getTotal());
                    provider.setReworkRate(reworkRate);
                    provider.setReworkTotal(reworkProvider.get().getReworkTotal());
                } else {
                    provider.setReworkTotal(0);
                    provider.setReworkRate("0");
                }
            }
        }
        return partProviderStat;
    }


    public PartStageStatus newPartStageStatus(BaseWarehouse baseWarehouse, BaseMaterial material, ProcessOrderDO processOrderDO,
                                              ProcessOrderDetailDO processOrderDetailDO, StockOperationEventData.StockMaterial stockDetail,
                                              Integer operation) {
        PartStageStatus partStageStatus = PartStageStatus.builder().stage(baseWarehouse.getStage())
                .worksheetId(processOrderDO.getId()).worksheetDetailId(processOrderDetailDO.getId())
                .projectCode(processOrderDO.getProjectCode()).worksheetCode(processOrderDO.getCode())
                .componentCode(processOrderDetailDO.getComponentCode()).componentName(processOrderDetailDO.getComponentName())
                .materialId(material.getId()).partCode(processOrderDetailDO.getCode()).partVersion(processOrderDetailDO.getVersion()).build();
        if (StockAction.IN.getId() == operation) {
            partStageStatus.setInStockTotal(stockDetail.getNumber().intValue());
            partStageStatus.setOutStockTotal(0);
            partStageStatus.setFirstInTime(new Date());
            partStageStatus.setLastInTime(new Date());
            partStageStatus.setStockNum(stockDetail.getNumber().intValue());
        } else {
            partStageStatus.setOutStockTotal(stockDetail.getNumber().intValue());
            partStageStatus.setInStockTotal(0);
            partStageStatus.setFirstOutTime(new Date());
            partStageStatus.setLastOutTime(new Date());
            partStageStatus.setStockNum(0);
        }
        return partStageStatus;
    }

    private PartStageStatus updatePartStageStatusUseNotWithFinish(PartStageStatus partStageStatus,
                                                                  StockOperationEventData.StockMaterial stockDetail) {
        partStageStatus.setOutStockTotal(partStageStatus.getOutStockTotal() + stockDetail.getNumber().intValue());
        partStageStatus.setLastOutTime(new Date());
        // 计算库存余量：入库总量-出库总量
        int stockNum = partStageStatus.getInStockTotal() - partStageStatus.getOutStockTotal();
        partStageStatus.setStockNum(Math.max(stockNum, 0));
        if (Objects.isNull(partStageStatus.getFirstInTime())) {
            partStageStatus.setFirstInTime(new Date());
        }
        if (Objects.isNull(partStageStatus.getFirstOutTime())) {
            partStageStatus.setFirstOutTime(new Date());
        }
        return partStageStatus;
    }

    private PartStageStatus updatePartStageStatus(PartStageStatus partStageStatus, StockOperationEventData.StockMaterial stockDetail,
                                                  Integer operation) {
        if (StockAction.IN.getId() == operation) {
            // 避免重复确认加工单操作，累加入库数量
            if (WarehouseStage.WAIT_RECEIVE.getId() == partStageStatus.getStage()) {
                partStageStatus.setInStockTotal(stockDetail.getNumber().intValue());
            } else {
                partStageStatus.setInStockTotal(partStageStatus.getInStockTotal() + stockDetail.getNumber().intValue());
            }
            partStageStatus.setLastInTime(new Date());
        } else {
            partStageStatus.setOutStockTotal(partStageStatus.getOutStockTotal() + stockDetail.getNumber().intValue());
            partStageStatus.setLastOutTime(new Date());
        }
        // 计算库存余量：入库总量-出库总量
        int stockNum = partStageStatus.getInStockTotal() - partStageStatus.getOutStockTotal();
        partStageStatus.setStockNum(Math.max(stockNum, 0));
        if (Objects.isNull(partStageStatus.getFirstInTime())) {
            partStageStatus.setFirstInTime(new Date());
        }
        if (Objects.isNull(partStageStatus.getFirstOutTime())) {
            partStageStatus.setFirstOutTime(new Date());
        }
        return partStageStatus;
    }

    @Override
    public MaterialWorksheetProgressStatResp progressStatistics(MaterialWorksheetProgressStatReq progressStatReq) {
        log.info("worksheet progress statistics start");
        MaterialWorksheetProgressStatResp progressStat = new MaterialWorksheetProgressStatResp();
        if (StrUtil.isBlank(progressStatReq.getProjectCode())) {
            // 项目代码为空，不查询
            log.info("worksheet progress statistics result is empty : project code is blank");
            return progressStat;
        }
        // 查询一个项目的加工单阶段状态相关信息
        List<MaterialWorksheetProgressListResp> worksheetProgressList = selectProgressList(progressStatReq);

        // 统计
        if (CollUtil.isNotEmpty(worksheetProgressList)) {
            // 组件进度统计
            List<MaterialWorksheetProgressStatResp.ProgressStat> progressStatList = componentStat(worksheetProgressList);
            // 项目进度统计
            projectStat(progressStatList);
            progressStat.setStatList(progressStatList);
        }

        return progressStat;
    }

    @Override
    public List<MaterialWorksheetProgressListResp> selectProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        log.info("worksheet progress list select");
        if (StrUtil.isBlank(progressStatReq.getProjectCode()) && StrUtil.isBlank(progressStatReq.getComponentCode())) {
            // 项目代码和组件号为空，不查询
            log.error("worksheet progress list select fail : project code and component code both blank");
            throw new ServiceException(StrUtil.format("零件进度列表查询失败:项目代码：{}，和组件号：{},不能同时为空。", progressStatReq.getProjectCode(),
                    progressStatReq.getComponentCode()));
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> partStageStatusListRespList = partStageStatusService.selectProgressList(progressStatReq);
        return handlePartsProgress(partStageStatusListRespList);
    }

    @Override
    public List<MaterialWorksheetProgressListResp> selectUnfinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        log.info("worksheet progress unfinished list select");
        if (StrUtil.isBlank(progressStatReq.getProjectCode()) && StrUtil.isBlank(progressStatReq.getComponentCode())) {
            // 项目代码和组件号为空，不查询
            log.error("worksheet progress list select fail : project code and component code both blank");
            throw new ServiceException(StrUtil.format("零件进度列表查询失败:项目代码：{}，和组件号：{},不能同时为空。", progressStatReq.getProjectCode(),
                    progressStatReq.getComponentCode()));
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> worksheetProgressFulInfoList = partStageStatusService.selectProgressList(progressStatReq);
        return handleUnfinishedPartsProgress(worksheetProgressFulInfoList);
    }

    @Override
    public List<MaterialWorksheetProgressListResp> selectFinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        log.info("worksheet progress finished list select");
        if (StrUtil.isBlank(progressStatReq.getProjectCode()) && StrUtil.isBlank(progressStatReq.getComponentCode())) {
            // 项目代码和组件号为空，不查询
            log.error("worksheet progress list select fail : project code and component code both blank");
            throw new ServiceException(StrUtil.format("零件进度列表查询失败:项目代码：{}，和组件号：{},不能同时为空。", progressStatReq.getProjectCode(),
                    progressStatReq.getComponentCode()));
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> finishedProgressListList = partStageStatusService.selectProgressList(progressStatReq);
        return handleFinishedPartsProgress(finishedProgressListList);
    }

    @Override
    public List<MaterialWorksheetProgressListResp> selectUsedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        log.info("worksheet progress used list select");
        if (StrUtil.isBlank(progressStatReq.getProjectCode()) && StrUtil.isBlank(progressStatReq.getComponentCode())) {
            // 项目代码和组件号为空，不查询
            log.error("worksheet progress list select fail : project code and component code both blank");
            throw new ServiceException(StrUtil.format("零件进度列表查询失败:项目代码：{}，和组件号：{},不能同时为空。", progressStatReq.getProjectCode(),
                    progressStatReq.getComponentCode()));
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> worksheetProgressFulInfoList = partStageStatusService.selectProgressList(progressStatReq);
        return handleUsedPartsProgress(worksheetProgressFulInfoList);
    }

    @Override
    public ProjectProgressR selectProjectProgress(PartProgressQuery partProgressQuery) {
        ProjectProgressR projectProgressR = new ProjectProgressR();
        if (StrUtil.isBlank(partProgressQuery.getProjectCode())) {
            return projectProgressR;
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> partStageStatusList = partStageStatusService.selectProgressList(MaterialWorksheetProgressStatReq.builder().projectCode(partProgressQuery.getProjectCode()).build());
        if (CollUtil.isEmpty(partStageStatusList)) {
            return projectProgressR;
        }
        projectProgressR.setProjectCode(partProgressQuery.getProjectCode());
        // 按阶段分组统计：已采购、已收件、已检验、已入库、已领用，每个步骤的零件和图纸
        Map<Integer, List<PartStageStatusListResp>> groupByStage = partStageStatusList.stream().collect(Collectors.groupingBy(PartStageStatusListResp::getStage));
        List<ProjectProgressR.ProgressStat> progressStatList = new ArrayList<>();
        projectProgressR.setStatList(progressStatList);
        // 总量
        double partTotal = 0;
        double paperTotal = 0;
        ProjectProgressR.ProgressStat purchasedProgress = ProjectProgressR.ProgressStat.builder().step(PartStep.PURCHASED.getId())
                .partTotal(0D).partRate("0%").paperTotal(0D).paperRate("0%").build();
        progressStatList.add(purchasedProgress);
        List<PartStageStatusListResp> purchasedList = groupByStage.get(WarehouseStage.WAIT_RECEIVE.getId());
        if (CollUtil.isNotEmpty(purchasedList)) {
            paperTotal = purchasedList.size();
            partTotal = purchasedList.stream().mapToInt(PartStageStatusListResp::getTotal).sum();
            // 已采购
            purchasedProgress.setPartTotal(partTotal);
            purchasedProgress.setPartRate("100%");
            purchasedProgress.setPaperTotal(paperTotal);
            purchasedProgress.setPaperRate("100%");
        }
        // 已收件
        projectReceivedProgressStat(partTotal, paperTotal, progressStatList, groupByStage);
        // 已检验
        projectCheckedProgressStat(partTotal, paperTotal, progressStatList, groupByStage);
        // 已入库
        projectFinishedProgressStat(partTotal, paperTotal, progressStatList, groupByStage);
        // 已领用
        projectUsedProgressStat(partTotal, paperTotal, progressStatList, groupByStage);
        return projectProgressR;
    }

    @Override
    public List<PartProgressR> selectPartProgress(PartProgressQuery partProgressQuery) {
        List<PartProgressR> partProgressRS = new ArrayList<>();
        if (StrUtil.isBlank(partProgressQuery.getProjectCode()) && StrUtil.isBlank(partProgressQuery.getPartCode())) {
            return partProgressRS;
        }
        // 查询一个项目的加工单，所有已确认零件进度相关信息
        List<PartStageStatusListResp> partStageStatusList = partStageStatusService.selectProgressList(MaterialWorksheetProgressStatReq.builder()
                .partCode(partProgressQuery.getPartCode()).projectCode(partProgressQuery.getProjectCode()).build());
        if (CollUtil.isEmpty(partStageStatusList)) {
            return partProgressRS;
        }
        // 按物料号（零件）分组，然后将零件的各阶段（进度）信息合并
        Map<Long, List<PartStageStatusListResp>> collect =
                partStageStatusList.stream().collect(Collectors.groupingBy(PartStageStatusListResp::getMaterialId));
        collect.forEach((materialId, list) -> {
            Optional<PartStageStatusListResp> waitReceive = list.stream().filter(a -> a.getStage() == WarehouseStage.WAIT_RECEIVE.getId()).findFirst();
            if (waitReceive.isPresent()) {
                PartProgressR partProgressR = partStageStatusAssembler.toPartProgressR(waitReceive.get());
                List<PartStageStatusListResp> checkedList = list.stream().filter(a -> a.getStage() == WarehouseStage.CHECKED_OK.getId()
                        || a.getStage() == WarehouseStage.WAIT_TREAT_SURFACE.getId() || a.getStage() == WarehouseStage.WAIT_REWORKED.getId()).collect(Collectors.toList());
                List<PartStageStatusListResp> finishedList = list.stream().filter(a -> a.getStage() == WarehouseStage.GOOD.getId()).collect(Collectors.toList());
                int checkedNum = checkedList.stream().mapToInt(PartStageStatusListResp::getInStockTotal).sum();
                int finishedNum = finishedList.stream().mapToInt(PartStageStatusListResp::getInStockTotal).sum();
                int usedNum = finishedList.stream().mapToInt(PartStageStatusListResp::getOutStockTotal).sum();
                partProgressR.setCheckedNum(checkedNum);
                partProgressR.setFinishedNum(finishedNum);
                partProgressR.setUsedNum(usedNum);
                partProgressR.setInspector(CollUtil.isNotEmpty(checkedList) ? checkedList.get(0).getCreateBy() : "");
                partProgressR.setFinishTime(CollUtil.isNotEmpty(finishedList) ? finishedList.get(0).getFirstInTime() : null);
                partProgressR.setReceivingTime(waitReceive.get().getFirstOutTime());
                partProgressRS.add(partProgressR);
            }
        });
        return partProgressRS;
    }

    @Override
    public ExcelWriter makeProjectPartProgressExcel(HttpServletResponse response, PartProgressQuery partProgressQuery) {
        ProjectProgressR projectProgressR = selectProjectProgress(partProgressQuery);
        List<PartProgressR> partProgressRList = selectPartProgress(partProgressQuery);
        ExcelWriter excelWriter = null;
        try {
            // 创建工作表
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            // 创建sheet1,sheet1里面有两个table
            // 把sheet设置为不需要头 不然会输出sheet的头 这样看起来第一个table 就有2个头了
            WriteSheet writeSheet1 = EasyExcel.writerSheet("项目进度").needHead(Boolean.FALSE).build();
            // 这里必须指定需要头，table 会继承sheet的配置，sheet配置了不需要，table 默认也是不需要
            // table0
            WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(ProjectProgressR.class).build();
            // table1
            WriteTable writeTable1 = EasyExcel.writerTable(1).needHead(Boolean.TRUE).head(ProjectProgressR.ProgressStat.class).build();
            // 第一次写入会创建头
            excelWriter.write(List.of(projectProgressR), writeSheet1, writeTable0);
            // 第二次写入也会创建头，然后在第一次的后面写入数据
            excelWriter.write(projectProgressR.getStatList(), writeSheet1, writeTable1);
            // 创建sheet2
            WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "零件进度").head(PartProgressR.class).build();
            excelWriter.write(partProgressRList, writeSheet2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelWriter;
    }

    @Override
    public void updatePartStageStatus(StockUpdateEventData stockUpdateEventData) {
        if (stockUpdateEventData.getWarehouse() != null) {
            PartStageStatus selectDO = PartStageStatus.builder().stage(stockUpdateEventData.getWarehouse().getStage())
                    .materialId(stockUpdateEventData.getMaterial().getId()).worksheetCode(stockUpdateEventData.getWorksheetCode())
                    .componentCode(stockUpdateEventData.getComponentCode()).build();
            PartStageStatus partStage = partStageStatusService.getOneOnly(selectDO);
            if (partStage != null) {
                partStage.setInStockTotal(Math.max(partStage.getInStockTotal() + stockUpdateEventData.getStockOffset().intValue(), 0));
                partStage.setStockNum(Math.max(partStage.getInStockTotal() - partStage.getOutStockTotal(), 0));
                partStageStatusService.updateById(partStage);
            } else {
                // 没找到则说明是新增的（需要无中生有）
                ProcessOrderDO processOrderDO = worksheetService.selectByCode(stockUpdateEventData.getWorksheetCode());
                if (Objects.isNull(processOrderDO)) {
                    throw new ServiceException(BizError.E25001);
                }
                ProcessOrderDetailDO selectDetailDO = ProcessOrderDetailDO.builder().materialId(stockUpdateEventData.getMaterial().getId()).
                        processOrderId(processOrderDO.getId()).
                        componentCode(stockUpdateEventData.getComponentCode()).
                        code(stockUpdateEventData.getPartCode()).
                        version(stockUpdateEventData.getPartVersion()).build();
                ProcessOrderDetailDO existDetail = worksheetDetailService.getOneOnly(selectDetailDO);
                if (Objects.isNull(existDetail)) {
                    throw new ServiceException(BizError.E25009);
                }
                selectDO.setWorksheetId(processOrderDO.getId());
                selectDO.setWorksheetDetailId(existDetail.getId());
                selectDO.setComponentCode(stockUpdateEventData.getComponentCode());
                selectDO.setComponentName(existDetail.getComponentName());
                selectDO.setPartCode(stockUpdateEventData.getPartCode());
                selectDO.setPartVersion(stockUpdateEventData.getPartVersion());
                selectDO.setProjectCode(stockUpdateEventData.getProjectCode());
                selectDO.setInStockTotal(Math.max(stockUpdateEventData.getStockOffset().intValue(), 0));
                selectDO.setStockNum(Math.max(stockUpdateEventData.getStockOffset().intValue(), 0));
                selectDO.setOutStockTotal(0);
                selectDO.setFirstInTime(new Date());
                selectDO.setLastInTime(new Date());
                partStageStatusService.save(selectDO);
            }
        }
    }


    public void setPartAndPaperOfInStock(List<PartStageStatusListResp> partStageList, ProjectProgressR.ProgressStat progressList, double partTotal, double paperTotal) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        double partNum = partStageList.stream().mapToInt(PartStageStatusListResp::getInStockTotal).sum();
        double paperNum = (int) partStageList.stream().filter(a -> a.getInStockTotal() == a.getTotal()).count();
        progressList.setPartTotal(partNum);
        progressList.setPartRate(decimalFormat.format(partNum / partTotal));
        progressList.setPaperTotal(paperNum);
        progressList.setPaperRate(decimalFormat.format(paperNum / paperTotal));
    }

    public void setPartAndPaperOfOutStock(List<PartStageStatusListResp> partStageList, ProjectProgressR.ProgressStat progressList, double partTotal, double paperTotal) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        double partNum = partStageList.stream().mapToInt(PartStageStatusListResp::getOutStockTotal).sum();
        double paperNum = (int) partStageList.stream().filter(a -> a.getOutStockTotal() == a.getTotal()).count();
        progressList.setPartTotal(partNum);
        progressList.setPartRate(decimalFormat.format(partNum / partTotal));
        progressList.setPaperTotal(paperNum);
        progressList.setPaperRate(decimalFormat.format(paperNum / paperTotal));
    }

    public void projectReceivedProgressStat(double partTotal, double paperTotal, List<ProjectProgressR.ProgressStat> progressStatList, Map<Integer, List<PartStageStatusListResp>> groupByStage) {
        ProjectProgressR.ProgressStat receivedProgress = ProjectProgressR.ProgressStat.builder().step(PartStep.RECEIVED.getId())
                .partTotal(0D).partRate("0%").paperTotal(0D).paperRate("0%").build();
        progressStatList.add(receivedProgress);
        List<PartStageStatusListResp> receivedList = groupByStage.get(WarehouseStage.WAIT_CHECK.getId());
        if (CollUtil.isNotEmpty(receivedList)) {
            setPartAndPaperOfInStock(receivedList, receivedProgress, partTotal, paperTotal);
        }
    }

    public void projectCheckedProgressStat(double partTotal, double paperTotal, List<ProjectProgressR.ProgressStat> progressStatList, Map<Integer, List<PartStageStatusListResp>> groupByStage) {
        ProjectProgressR.ProgressStat checkedProgress = ProjectProgressR.ProgressStat.builder().step(PartStep.CHECKED.getId())
                .partTotal(0D).partRate("0%").paperTotal(0D).paperRate("0%").build();
        progressStatList.add(checkedProgress);
        List<PartStageStatusListResp> checkedList = new ArrayList<>();
        groupByStage.forEach((stage, list) -> {
            if (stage == WarehouseStage.CHECKED_OK.getId() || stage == WarehouseStage.WAIT_TREAT_SURFACE.getId() || stage == WarehouseStage.WAIT_REWORKED.getId()) {
                checkedList.addAll(list);
            }
        });
        if (CollUtil.isNotEmpty(checkedList)) {
            setPartAndPaperOfInStock(checkedList, checkedProgress, partTotal, paperTotal);
        }
    }

    public void projectFinishedProgressStat(double partTotal, double paperTotal, List<ProjectProgressR.ProgressStat> progressStatList, Map<Integer, List<PartStageStatusListResp>> groupByStage) {
        ProjectProgressR.ProgressStat receivedProgress = ProjectProgressR.ProgressStat.builder().step(PartStep.FINISHED.getId())
                .partTotal(0D).partRate("0%").paperTotal(0D).paperRate("0%").build();
        progressStatList.add(receivedProgress);
        List<PartStageStatusListResp> receivedList = groupByStage.get(WarehouseStage.GOOD.getId());
        if (CollUtil.isNotEmpty(receivedList)) {
            setPartAndPaperOfInStock(receivedList, receivedProgress, partTotal, paperTotal);
        }
    }

    public void projectUsedProgressStat(double partTotal, double paperTotal, List<ProjectProgressR.ProgressStat> progressStatList, Map<Integer, List<PartStageStatusListResp>> groupByStage) {
        ProjectProgressR.ProgressStat receivedProgress = ProjectProgressR.ProgressStat.builder().step(PartStep.USED.getId())
                .partTotal(0D).partRate("0%").paperTotal(0D).paperRate("0%").build();
        progressStatList.add(receivedProgress);
        List<PartStageStatusListResp> receivedList = groupByStage.get(WarehouseStage.GOOD.getId());
        if (CollUtil.isNotEmpty(receivedList)) {
            setPartAndPaperOfOutStock(receivedList, receivedProgress, partTotal, paperTotal);
        }
    }

    private List<MaterialWorksheetProgressListResp> handlePartsProgress(List<PartStageStatusListResp> partStageStatusListRespList) {
        List<MaterialWorksheetProgressListResp> worksheetProgressListResp = new ArrayList<>();
        if (CollUtil.isNotEmpty(partStageStatusListRespList)) {
            // 未完成
            worksheetProgressListResp.addAll(handleUnfinishedPartsProgress(partStageStatusListRespList));
            // 已完成
            worksheetProgressListResp.addAll(handleFinishedPartsProgress(partStageStatusListRespList));
            // 已领用
            worksheetProgressListResp.addAll(handleUsedPartsProgress(partStageStatusListRespList));
        }
        // 合并零件进度：全部=未完成+已完成+已领用
        return mergePartsProgress(worksheetProgressListResp);
    }

    private List<MaterialWorksheetProgressListResp> mergePartsProgress(List<MaterialWorksheetProgressListResp> worksheetProgressListResp) {
        List<MaterialWorksheetProgressListResp> materialWorksheetProgressListRespList = new ArrayList<>();
        // 按物料号（零件）分组，然后将每种零件的阶段（进度）信息合并
        Map<Long, List<MaterialWorksheetProgressListResp>> collect =
                worksheetProgressListResp.stream().collect(Collectors.groupingBy(MaterialWorksheetProgressListResp::getMaterialId));
        collect.forEach((materialId, list) -> {
            List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList = new ArrayList<>();
            for (MaterialWorksheetProgressListResp progressListResp : list) {
                worksheetProgressList.addAll(progressListResp.getProgressList());
            }
            MaterialWorksheetProgressListResp materialWorksheetProgressListResp = list.get(0);
            materialWorksheetProgressListResp.setProgressList(worksheetProgressList);
            // 找已完成的时间
            Optional<MaterialWorksheetProgressListResp> finishProgress =
                    list.stream().filter(a -> a.getStage().equals(PartProgressType.YWC.getType())).findFirst();
            finishProgress.ifPresent(progressListResp -> materialWorksheetProgressListResp.setFinishTime(progressListResp.getFinishTime()));
            materialWorksheetProgressListRespList.add(materialWorksheetProgressListResp);
        });
        return materialWorksheetProgressListRespList;
    }


    private List<MaterialWorksheetProgressListResp> handleUnfinishedPartsProgress(List<PartStageStatusListResp> progressListList) {
        List<PartStageStatusListResp> unfinishedProgressListList = new ArrayList<>();
        if (CollUtil.isNotEmpty(progressListList)) {
            // 过滤出未完成的阶段=非已完成阶段且库存大于0
            progressListList =
                    progressListList.stream().filter(a -> !a.getStage().equals(PartProgressType.YWC.getType()) && a.getStockNum() > 0).collect(Collectors.toList());
            // 按物料号分组，展示一种物料的多阶段状态
            Map<Long, List<PartStageStatusListResp>> collect =
                    progressListList.stream().collect(Collectors.groupingBy(PartStageStatusListResp::getMaterialId));
            collect.forEach((materialId, list) -> {
                PartStageStatusListResp partStageStatusListResp = list.get(0);
                unfinishedProgressListList.add(partStageStatusListResp);
                List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList = new ArrayList<>();
                partStageStatusListResp.setProgressList(worksheetProgressList);
                List<PartStageStatusListResp.Stock> stockList = new ArrayList<>();
                partStageStatusListResp.setStockList(stockList);
                for (PartStageStatusListResp stageStatusListResp : list) {
                    handleWorksheetProgress(worksheetProgressList, stageStatusListResp.getStage(), stageStatusListResp.getStockNum());
                }
                handleStockList(stockList, materialId);
            });
        }
        return partStageStatusAssembler.toWorksheetProgressListResp(unfinishedProgressListList);
    }

    private List<MaterialWorksheetProgressListResp> handleFinishedPartsProgress(List<PartStageStatusListResp> progressListList) {
        if (CollUtil.isNotEmpty(progressListList)) {
            // 过滤出已完成的阶段=已完成阶段且入库数量大于0
            progressListList =
                    progressListList.stream().filter(a -> a.getStage().equals(PartProgressType.YWC.getType()) && a.getInStockTotal() > 0).collect(Collectors.toList());
            for (PartStageStatusListResp partStageStatusListResp : progressListList) {
                // 补充查询进度详情数据
                List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList = handleWorksheetProgress(null,
                        PartProgressType.YWC.getType(), partStageStatusListResp.getInStockTotal());
                partStageStatusListResp.setProgressList(worksheetProgressList);

                // 补充查询仓库库存数据
                partStageStatusListResp.setStockList(handleStockList(null, partStageStatusListResp.getMaterialId()));

                // 设置完成时间
                partStageStatusListResp.setFinishTime(partStageStatusListResp.getFirstInTime());
            }
        }
        return partStageStatusAssembler.toWorksheetProgressListResp(progressListList);
    }

    private List<MaterialWorksheetProgressListResp> handleUsedPartsProgress(List<PartStageStatusListResp> progressListList) {
        if (CollUtil.isNotEmpty(progressListList)) {
            // 过滤出已领用的阶段=已完成阶段且出库数量大于0
            progressListList =
                    progressListList.stream().filter(a -> a.getStage().equals(PartProgressType.YWC.getType()) && a.getOutStockTotal() > 0).collect(Collectors.toList());
            for (PartStageStatusListResp partStageStatusListResp : progressListList) {
                // 补充查询进度详情数据
                List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList = handleWorksheetProgress(null,
                        PartProgressType.YLY.getType(), partStageStatusListResp.getOutStockTotal());
                partStageStatusListResp.setProgressList(worksheetProgressList);

                // 补充查询仓库库存数据
                partStageStatusListResp.setStockList(handleStockList(null, partStageStatusListResp.getMaterialId()));
            }
        }
        return partStageStatusAssembler.toWorksheetProgressListResp(progressListList);
    }


    /**
     * 处理进度
     */
    private List<PartStageStatusListResp.WorksheetProgress> handleWorksheetProgress(List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList, int type, Integer num) {
        if (worksheetProgressList == null) {
            worksheetProgressList = new ArrayList<>();
        }
        // 补充进度详情数据
        PartStageStatusListResp.WorksheetProgress worksheetProgress = PartStageStatusListResp.WorksheetProgress.builder()
                .progress(PartProgressType.getNameByType(type).getName()).number(num).build();
        worksheetProgressList.add(worksheetProgress);
        return worksheetProgressList;
    }

    /**
     * 处理库存
     */
    private List<PartStageStatusListResp.Stock> handleStockList(List<PartStageStatusListResp.Stock> stockList, Long materialId) {
        if (stockList == null) {
            stockList = new ArrayList<>();
        }
        List<MaterialStock> materialStockList = stockService.list(Wrappers.query(MaterialStock.builder().materialId(materialId).build()));
        for (MaterialStock materialStock : materialStockList) {
            BaseWarehouse warehouse = warehouseService.selectBaseWarehouseById(materialStock.getWarehouseId());
            if (Objects.isNull(warehouse)) {
                log.error("warehouse not found by id: {}", materialStock.getWarehouseId());
                throw new ServiceException(BizError.E23001);
            }
            PartStageStatusListResp.Stock stock =
                    PartStageStatusListResp.Stock.builder().warehouseName(warehouse.getName()).number(materialStock.getNumber().intValue()).build();
            stockList.add(stock);
        }
        return stockList;
    }


    /**
     * 组件进度统计
     */
    private List<MaterialWorksheetProgressStatResp.ProgressStat> componentStat(List<MaterialWorksheetProgressListResp> worksheetProgressList) {
        List<MaterialWorksheetProgressStatResp.ProgressStat> progressStatList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        // 按组件分组
        Map<String, List<MaterialWorksheetProgressListResp>> collect =
                worksheetProgressList.stream().collect(Collectors.groupingBy(MaterialWorksheetProgressListResp::getComponentCode));
        // 进度统计的类型：包含多个组件为组件，只包含一个组件则是项目
        String progressType;
        if (collect.size() > 1) {
            progressType = ProjectProgressType.COMPONENT.getType();
        } else {
            progressType = ProjectProgressType.PROJECT.getType();
        }
        collect.forEach((componentCode, list) -> {
            // 图纸统计：图纸描述的是一种组件里的一个零件
            Map<String, List<MaterialWorksheetProgressListResp>> paperCollect = list.stream().collect(Collectors.groupingBy(p -> p.getPartCode() +
                    "_" + p.getPartVersion()));
            // 零件总数量
            AtomicDouble partTotal = new AtomicDouble();
            // 零件待收件总数量
            AtomicDouble partToReceivedNum = new AtomicDouble();
            // 零件已完成总数量
            AtomicDouble partFinishedNum = new AtomicDouble();
            // 零件已领用总数量
            AtomicDouble partUsedNum = new AtomicDouble();
            // 图纸待收件总数量
            AtomicDouble paperToReceivedNum = new AtomicDouble();
            // 图纸已完成总数量
            AtomicDouble paperFinishedNum = new AtomicDouble();
            // 图纸已领用总数量
            AtomicDouble paperUsedNum = new AtomicDouble();
            paperCollect.forEach((codeVersion, list1) -> {
                double toReceivedNumTemp = 0;
                double finishedNumTemp = 0;
                double usedNumTemp = 0;
                double totalTemp = 0;
                for (MaterialWorksheetProgressListResp materialWorksheetProgress : list1) {
                    List<PartStageStatusListResp.WorksheetProgress> progressList = materialWorksheetProgress.getProgressList();
                    toReceivedNumTemp += statByPartProgressType(progressList, PartProgressType.DSJ.getName());
                    finishedNumTemp += statByPartProgressType(progressList, PartProgressType.YWC.getName());
                    usedNumTemp += statByPartProgressType(progressList, PartProgressType.YLY.getName());
                    totalTemp += materialWorksheetProgress.getTotal();
                }
                partToReceivedNum.addAndGet(toReceivedNumTemp);
                partFinishedNum.addAndGet(finishedNumTemp);
                partUsedNum.addAndGet(usedNumTemp);
                partTotal.addAndGet(totalTemp);
                // 总数量==待收件数量：图纸状态为待收件
                if (totalTemp == toReceivedNumTemp) {
                    paperToReceivedNum.addAndGet(1);
                }
                // 总数量==已完成数量：图纸状态为已完成
                if (totalTemp == finishedNumTemp) {
                    paperFinishedNum.addAndGet(1);
                }
                // 总数量==已领用数量：图纸状态为已领用
                if (totalTemp == usedNumTemp) {
                    paperUsedNum.addAndGet(1);
                }
            });
            // 图纸类型总数
            int paperTotal = paperCollect.size();
            MaterialWorksheetProgressStatResp.ProgressStat progress =
                    MaterialWorksheetProgressStatResp.ProgressStat.builder().projectCode(list.get(0).getProjectCode())
                            .componentCode(componentCode).componentName(list.get(0).getComponentName())
                            .progressType(progressType)
                            .partTotal(partTotal.get())
                            .partToReceivedTotal(partToReceivedNum.get()).partToReceivedRate(decimalFormat.format(partToReceivedNum.get() / partTotal.get()))
                            .partFinishedTotal(partFinishedNum.get()).partFinishedRate(decimalFormat.format(partFinishedNum.get() / partTotal.get()))
                            .partUsedTotal(partUsedNum.get()).partUsedRate(decimalFormat.format(partUsedNum.get() / partTotal.get()))
                            .paperTotal((double) paperTotal)
                            .paperToReceivedTotal(paperToReceivedNum.get()).paperToReceivedRate(decimalFormat.format(paperToReceivedNum.get() / paperTotal))
                            .paperFinishedTotal(paperFinishedNum.get()).paperFinishedRate(decimalFormat.format(paperFinishedNum.get() / paperTotal))
                            .paperUsedTotal(paperUsedNum.get()).paperUsedRate(decimalFormat.format(paperUsedNum.get() / paperTotal))
                            .build();
            progressStatList.add(progress);
        });
        return progressStatList;
    }

    private int statByPartProgressType(List<PartStageStatusListResp.WorksheetProgress> worksheetProgressList, String type) {
        Optional<PartStageStatusListResp.WorksheetProgress> first =
                worksheetProgressList.stream().filter(a -> a.getProgress().equals(type)).findFirst();
        if (first.isPresent()) {
            PartStageStatusListResp.WorksheetProgress worksheetProgress = first.get();
            return worksheetProgress.getNumber();
        }
        return 0;
    }

    /**
     * 项目进度统计
     */
    private void projectStat(List<MaterialWorksheetProgressStatResp.ProgressStat> progressStatList) {
        // 进度统计包含多个，说明项目包含多个组件
        if (progressStatList.size() > 1) {
            DecimalFormat decimalFormat = new DecimalFormat("0.##%");
            double toReceivedNumPart = 0;
            double finishedNumPart = 0;
            double usedNumPart = 0;
            double totalPart = 0;
            double toReceivedNumPaper = 0;
            double finishedNumPaper = 0;
            double usedNumPaper = 0;
            double totalPaper = 0;
            for (MaterialWorksheetProgressStatResp.ProgressStat progressStat : progressStatList) {
                toReceivedNumPart += progressStat.getPartToReceivedTotal();
                finishedNumPart += progressStat.getPartFinishedTotal();
                usedNumPart += progressStat.getPartUsedTotal();
                totalPart += progressStat.getPartTotal();
                toReceivedNumPaper += progressStat.getPaperToReceivedTotal();
                finishedNumPaper += progressStat.getPaperFinishedTotal();
                usedNumPaper += progressStat.getPaperUsedTotal();
                totalPaper += progressStat.getPaperTotal();
            }
            MaterialWorksheetProgressStatResp.ProgressStat projectProgress =
                    MaterialWorksheetProgressStatResp.ProgressStat.builder().progressType(ProjectProgressType.PROJECT.getType())
                            .projectCode(progressStatList.get(0).getProjectCode())
                            .partTotal(totalPart)
                            .partToReceivedTotal(toReceivedNumPart).partToReceivedRate(decimalFormat.format(toReceivedNumPart / totalPart))
                            .partFinishedTotal(finishedNumPart).partFinishedRate(decimalFormat.format(finishedNumPart / totalPart))
                            .partUsedTotal(usedNumPart).partUsedRate(decimalFormat.format(usedNumPart / totalPart))
                            .paperTotal(totalPaper)
                            .paperToReceivedTotal(toReceivedNumPaper).paperToReceivedRate(decimalFormat.format(toReceivedNumPaper / totalPaper))
                            .paperFinishedTotal(finishedNumPaper).paperFinishedRate(decimalFormat.format(finishedNumPaper / totalPaper))
                            .paperUsedTotal(usedNumPaper).paperUsedRate(decimalFormat.format(usedNumPaper / totalPaper))
                            .build();
            progressStatList.add(projectProgress);
        }
    }

}
