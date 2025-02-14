package com.greenstone.mes.machine.domain.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockRecordSaveCommand;
import com.greenstone.mes.machine.application.service.MachineStockRecordDetailService;
import com.greenstone.mes.machine.application.service.MachineStockRecordService;
import com.greenstone.mes.machine.domain.service.MachineStockRecordManager;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecord;
import com.greenstone.mes.machine.infrastructure.persistence.MachineStockRecordDetail;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.response.MaterialInfoResp;
import com.greenstone.mes.material.response.StockRecordDetailResp;
import com.greenstone.mes.warehouse.domain.StockCmd;
import com.greenstone.mes.warehouse.domain.StockMaterial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MachineStockRecordManagerImpl implements MachineStockRecordManager {

    @Autowired
    private MachineStockRecordService stockRecordService;

    @Autowired
    private MachineStockRecordDetailService stockRecordDetailService;

    @Override
    public void saveStockRecord(StockCmd stockCmd) {
        // 保存出入库记录
        MachineStockRecord stockRecord = MachineStockRecord.builder()
                .operation(stockCmd.getOperation().getId())
                .serialNo(stockCmd.getSerialNo())
                .sponsor(stockCmd.getSponsor())
                .applicant(stockCmd.getApplicant())
                .applicantNo(stockCmd.getApplicantNo())
                .remark(stockCmd.getRemark()).build();
        stockRecordService.save(stockRecord);

        // 保存存取记录详情
        List<MachineStockRecordDetail> stockRecordDetailList = new ArrayList<>();
        List<StockMaterial> stockDetailList = stockCmd.getMaterialList();
        for (StockMaterial stockDetail : stockDetailList) {
            MachineStockRecordDetail stockRecordDetail = MachineStockRecordDetail.builder()
                    .recordId(stockRecord.getId())
                    .projectCode(stockDetail.getProjectCode())
                    .warehouseId(stockDetail.getWarehouse().getId())
                    .sponsor(stockRecord.getSponsor())
                    .applicant(stockRecord.getApplicant())
                    .applicantNo(stockRecord.getApplicantNo())
                    .operation(stockCmd.getOperation().getId())
                    .action(stockDetail.getAction().getId())
                    .stage(stockDetail.getWarehouse().getStage())
                    .behavior(stockDetail.getBehavior().getId())
                    .materialId(stockDetail.getMaterial().getId())
                    .number(stockDetail.getNumber()).orderSerialNo(stockDetail.getOrderSerialNo()).build();
            // 入库比出库晚1s
            if (stockDetail.getAction() == StockAction.IN) {
                stockRecordDetail.setCreateTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 1, ChronoUnit.SECONDS));
            } else {
                stockRecordDetail.setCreateTime(LocalDateTime.now());
            }
            stockRecordDetailList.add(stockRecordDetail);
        }
        stockRecordDetailService.saveBatch(stockRecordDetailList, 100);
    }

    @Override
    public void saveStockRecord(MachineStockRecordSaveCommand stockRecordSaveCommand) {
        // 全部出库，保存其绑定的库位的出入库记录
        BaseWarehouse warehouse = stockRecordSaveCommand.getWarehouse();
        // 保存出入库记录
        MachineStockRecord stockRecord = MachineStockRecord.builder().
                sponsor(stockRecordSaveCommand.getSponsor()).
                applicant(StrUtil.isEmpty(stockRecordSaveCommand.getApplicant()) ? SecurityUtils.getLoginUser() == null ? "sys" : SecurityUtils.getLoginUser().getUser().getNickName() : stockRecordSaveCommand.getApplicant()).
                applicantNo(StrUtil.isEmpty(stockRecordSaveCommand.getApplicantNo()) ? SecurityUtils.getLoginUser() == null ? "" : SecurityUtils.getLoginUser().getUser().getEmployeeNo() : stockRecordSaveCommand.getApplicantNo()).
                remark(stockRecordSaveCommand.getRemark()).
                operation(stockRecordSaveCommand.getAction().getId()).build();
        stockRecordService.save(stockRecord);

        // 保存存取记录详情
        List<MachineStockRecordDetail> stockRecordDetailList = new ArrayList<>();
        List<MachineStockRecordSaveCommand.StockDetail> stockDetailList = stockRecordSaveCommand.getMaterialList();
        for (MachineStockRecordSaveCommand.StockDetail stockDetail : stockDetailList) {
            MachineStockRecordDetail stockRecordDetail = MachineStockRecordDetail.builder().recordId(stockRecord.getId()).projectCode(stockDetail.getProjectCode()).
                    warehouseId(warehouse.getId()).
                    sponsor(stockRecord.getSponsor()).
                    applicant(stockRecord.getApplicant()).
                    applicantNo(stockRecord.getApplicantNo()).
                    stage(stockRecordSaveCommand.getOperation() == null ? null : stockRecordSaveCommand.getOperation().getId()).
                    materialId(stockDetail.getMaterial().getId()).
                    number(stockDetail.getNumber()).orderSerialNo(stockDetail.getOrderSerialNo()).build();
            // 入库比出库晚1s
            if (stockRecordDetail.getAction() == 0) {
                stockRecordDetail.setCreateTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 1, ChronoUnit.SECONDS));
            } else {
                stockRecordDetail.setCreateTime(LocalDateTime.now());
            }
            stockRecordDetailList.add(stockRecordDetail);
        }
        stockRecordDetailService.saveBatch(stockRecordDetailList, 100);
    }

    @Override
    public StockRecordDetailResp getRecordDetail(Long recordId) {
        MachineStockRecord stockRecord = stockRecordService.getById(recordId);
        if (Objects.isNull(stockRecord)) {
            throw new ServiceException("stock.record.does.not.exist");
        }
        BaseWarehouse warehouse = null; //warehouseService.selectBaseWarehouseById(stockRecord.getWarehouseId());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException("the.warehouse.does.not.exist");
        }

        StockRecordDetailResp recordDetailResp = new StockRecordDetailResp();
        recordDetailResp.setSponsor(stockRecord.getSponsor());
        recordDetailResp.setOperator(stockRecord.getCreateBy());
        recordDetailResp.setWarehouseName(warehouse.getName());

        recordDetailResp.setOperationTime(stockRecord.getCreateTime());
        recordDetailResp.setRemark(stockRecord.getRemark());

        List<MaterialInfoResp> materialList = stockRecordDetailService.listStockRecordDetail(stockRecord.getId());
        recordDetailResp.setMaterialList(materialList);

        return recordDetailResp;
    }

}
