package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.machine.application.assemble.MachineWarehouseOutAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineWarehouseOutAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineStockAllQuery;
import com.greenstone.mes.machine.application.dto.event.MachineWarehouseOutE;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecord;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutRecordExportR;
import com.greenstone.mes.machine.application.dto.result.MachineWarehouseOutResult;
import com.greenstone.mes.machine.application.event.MachineWarehouseOutEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineStockService;
import com.greenstone.mes.machine.application.service.MachineWarehouseOutService;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseOut;
import com.greenstone.mes.machine.domain.entity.MachineWarehouseOutDetail;
import com.greenstone.mes.machine.domain.helper.StockVoHelper;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineRequirementOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineWarehouseOutRepository;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.office.api.RemoteWxApprovalService;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.warehouse.domain.StockPrepareCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-29-11:30
 */
@AllArgsConstructor
@Slf4j
@Service
public class MachineWarehouseOutServiceImpl implements MachineWarehouseOutService {

    private final MachineWarehouseOutRepository warehouseOutRepository;
    private final MachineWarehouseOutAssemble warehouseOutAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineOrderOldRepository orderRepository;
    private final MachineRequirementOldRepository requirementRepository;
    private final MachineStockService stockService;
    private final MachineHelper machineHelper;
    private final RemoteWxApprovalService wxApprovalService;
    private final StockVoHelper stockVoHelper;

    @Transactional
    @Override
    public void saveDraft(MachineWarehouseOutAddCmd addCmd) {
        log.info("machine warehouse out save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineWarehouseOut warehouseOut = validAndAssembleWarehouseOut(addCmd, isNew, false);
        warehouseOut.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            warehouseOutRepository.add(warehouseOut);
        } else {
            warehouseOutRepository.edit(warehouseOut);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineWarehouseOutAddCmd addCmd) {
        log.info("machine warehouse out save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineWarehouseOut warehouseOut = validAndAssembleWarehouseOut(addCmd, isNew, true);
        warehouseOut.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            warehouseOutRepository.add(warehouseOut);
        } else {
            warehouseOutRepository.edit(warehouseOut);
        }
        // 提交后操作
        eventPublisher.publishEvent(new MachineWarehouseOutEvent(warehouseOutAssemble.toWarehouseOutE(warehouseOut)));
    }

    public MachineWarehouseOut validAndAssembleWarehouseOut(MachineWarehouseOutAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineWarehouseOut warehouseOut = warehouseOutAssemble.toMachineWarehouseOut(addCmd);
        if (isCommit) {
            for (MachineWarehouseOutDetail part : warehouseOut.getParts()) {
                // 校验零件
                machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
                // 校验仓库
                machineHelper.existWarehouseByCode(part.getWarehouseCode());
                if (!addCmd.isForceOperation()) {
                    // 查询库存
                    Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), part.getWarehouseCode());
                    if (stockNumber < part.getOutStockNumber()) {
                        throw new ServiceException(MachineError.E200014, StrUtil.format("项目号：{}，零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                                part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getOutStockNumber()));
                    }
                }
            }
        }
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_warehouseOut").prefix("MWO" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            warehouseOut.setSerialNo(serialNoR.getSerialNo());
        }
        warehouseOut.getParts().forEach(p -> p.setSerialNo(warehouseOut.getSerialNo()));
        return warehouseOut;
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        warehouseOutRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineWarehouseOutResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine warehouseOut list params:{}", query);
        List<MachineWarehouseOut> list = warehouseOutRepository.list(query);
        return warehouseOutAssemble.toMachineWarehouseOutRs(list);
    }


    @Override
    public MachineWarehouseOutResult detail(String serialNo) {
        log.info("query machine warehouseOut detail params:{}", serialNo);
        MachineWarehouseOut detail = warehouseOutRepository.detail(serialNo);
        return warehouseOutAssemble.toMachineWarehouseOutR(detail);
    }

    @Override
    public List<MachinePartStockR> stockAll(MachineStockAllQuery query) {
//        BaseWarehouse warehouse = machineHelper.existWarehouseByCode(query.getWarehouseCode());
//        if (warehouse.getStage() != PartStage.FINISHED.getId()) {
//            throw new ServiceException(MachineError.E200009, StrUtil.format("出库单只能选择良品库出库"));
//        }
        return machineHelper.getStockAllByWarehouse(query.getWarehouseCode());
    }

    @Override
    public String sign(MachineSignCmd signCmd) {
        log.info("发送出库签名:{}", signCmd);
        MachineWarehouseOut warehouseOut = warehouseOutRepository.getMachineWarehouseOut(signCmd.getSerialNo());
        if (warehouseOut.getStatus() != ProcessStatus.REJECTED && StrUtil.isNotEmpty(warehouseOut.getSpNo())) {
            throw new ServiceException(StrUtil.format("已发送签字审批，请前往企业微信签字，单号：{}", warehouseOut.getSpNo()));
        }
        WxApprovalFinishedCommitCmd commitCmd = warehouseOutAssemble.toFinishedApprovalCmd(warehouseOut);
        log.info("参数：{}", commitCmd);
        R<String> data = wxApprovalService.commitFinishedApproval(commitCmd);
        if (data.isFail()) {
            throw new ServiceException(data.getMsg());
        }
        String spNo = data.getMsg();
        warehouseOutRepository.sign(warehouseOut.getSerialNo(), spNo);
        return spNo;
    }

    @Override
    public void signFinish(MachineSignFinishCmd finishCmd) {
        MachineWarehouseOut warehouseOut = warehouseOutRepository.detail(finishCmd.getSerialNo());
        warehouseOutRepository.signFinish(finishCmd);
        if (finishCmd.getStatus() == ProcessStatus.FINISH) {
            // 出库零件操作
            MachineWarehouseOutE machineWarehouseOutE = warehouseOutAssemble.toWarehouseOutE(warehouseOut);
            log.info("签字完成，开始转移库存：{}", machineWarehouseOutE);
            operationAfterWarehouseOut(machineWarehouseOutE);
        }
    }

    @Override
    public SysFile print(String serialNo) {
        MachineWarehouseOut detail = warehouseOutRepository.detail(serialNo);
        return machineHelper.warehouseOutGenWord(detail);
    }

    @Override
    public List<MachineWarehouseOutRecord> listRecord(MachineRecordQuery query) {
        if (query.getEndDate() != null) {
            query.setEndDate(cn.hutool.core.date.DateUtil.endOfDay(query.getEndDate()));
        }
        return warehouseOutRepository.listRecord(query);
    }

    @Override
    public List<MachineWarehouseOutRecordExportR> exportRecord(MachineRecordQuery query) {
        return warehouseOutAssemble.toMachineWarehouseOutRecordERS(listRecord(query));
    }

    @Override
    public void operationAfterWarehouseOut(MachineWarehouseOutE source) {
        log.info("创建出库单，开始处理库存数据: {}", source);
        StockPrepareCmd stockPrepareCmd = stockVoHelper.converterStockCmd(source);
        stockService.doStock(stockPrepareCmd);
        log.info("创建出库单，处理库存数据完成");

    }

}
