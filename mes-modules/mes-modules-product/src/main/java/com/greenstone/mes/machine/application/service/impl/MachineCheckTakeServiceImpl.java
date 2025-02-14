package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.machine.application.assemble.MachineCheckTakeAssemble;
import com.greenstone.mes.machine.application.assemble.MachineOrderAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckTakeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.event.MachineCheckTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineCheckTakeResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.event.MachineCheckTakeEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineCheckTakeService;
import com.greenstone.mes.machine.domain.entity.MachineCheckTake;
import com.greenstone.mes.machine.domain.entity.MachineCheckTakeDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.repository.MachineCheckTakeRepository;
import com.greenstone.mes.machine.domain.repository.MachineOrderOldRepository;
import com.greenstone.mes.machine.domain.service.MachineStockManager;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.office.api.RemoteWxApprovalService;
import com.greenstone.mes.system.api.domain.SysFile;
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
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class MachineCheckTakeServiceImpl implements MachineCheckTakeService {

    private final MachineCheckTakeRepository checkRepository;
    private final MachineCheckTakeAssemble checkAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineStockManager stockManager;
    private final MachineOrderOldRepository orderRepository;
    private final MachineOrderAssemble orderAssemble;
    private final MachineHelper machineHelper;
    private final RemoteWxApprovalService wxApprovalService;

    @Transactional
    @Override
    public void saveDraft(MachineCheckTakeAddCmd addCmd) {
        log.info("machine check take save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheckTake check = validAndAssembleCheck(addCmd, isNew, false);
        check.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineCheckTakeAddCmd addCmd) {
        log.info("machine check take save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheckTake check = validAndAssembleCheck(addCmd, isNew, true);
        check.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
        // 提交后操作
        eventPublisher.publishEvent(new MachineCheckTakeEvent(checkAssemble.toCheckTakeE(check)));
    }

    public MachineCheckTake validAndAssembleCheck(MachineCheckTakeAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineCheckTake check = checkAssemble.toMachineCheckTake(addCmd);
        if (isCommit) {
            checkPart(check, addCmd.isForceOperation());
        }
        setApplyInfo(check);
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_check_take").prefix("MCT" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            check.setSerialNo(serialNoR.getSerialNo());
        }
        check.getParts().forEach(p -> p.setSerialNo(check.getSerialNo()));
        return check;
    }

    public void checkPart(MachineCheckTake check, boolean forceOperation) {
        for (MachineCheckTakeDetail part : check.getParts()) {
            // 校验零件
            machineHelper.checkMaterial(part.getPartCode(), part.getPartVersion());
            // 校验仓库
            machineHelper.existWarehouseByCode(part.getOutWarehouseCode());
            if (!forceOperation) {
                // 查询库存
                Long stockNumber = machineHelper.getStockNumberWithProjectCode(part.getProjectCode(), part.getMaterialId(), part.getOutWarehouseCode());
                if (stockNumber < part.getTakeNumber()) {
                    throw new ServiceException(MachineError.E200014, StrUtil.format("项目号：{}，零件号/版本：{}/{}，库存数量：{}，出库数量：{}",
                            part.getProjectCode(), part.getPartCode(), part.getPartVersion(), stockNumber, part.getTakeNumber()));
                }
            }
        }
    }

    @Override
    public MachineOrderPartR scan(MachineOrderPartScanQuery query) {
        log.info("scan part from machine check params:{}", query);
        if (StrUtil.isBlank(query.getRequirementSerialNo()) && StrUtil.isBlank(query.getSerialNo())) {
            throw new ServiceException("机加工需求单号和订单号不能都为空");
        }
        // 校验仓库
        machineHelper.existWarehouseByCode(query.getWarehouseCode());
        // 校验订单
        MachineOrderDetail orderDetail = orderRepository.selectPart(query);
        if (Objects.isNull(orderDetail)) {
            throw new ServiceException(MachineError.E200108, StrUtil.format("零件号/版本：{}/{}", query.getPartCode(), query.getPartVersion()));
        }
//        if (Objects.isNull(orderDetail.getReceivedNumber())) {
//            throw new ServiceException(MachineError.E200008, StrUtil.format("零件号/版本：{}/{}", query.getPartCode(), query.getPartVersion()));
//        }
        MachineOrderPartR machineOrderPartR = orderAssemble.toMachineOrderPartR(orderDetail);
        // 获取库存数量
        machineOrderPartR.setStockNumber(machineHelper.getStockNumber(orderDetail.getMaterialId(), query.getWarehouseCode()));
        return machineOrderPartR;
    }

    @Override
    public SysFile print(String serialNo) {
        MachineCheckTake detail = checkRepository.detail(serialNo);
        return machineHelper.checkTakeGenWord(detail);
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        checkRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineCheckTakeResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine check take list params:{}", query);
        List<MachineCheckTake> list = checkRepository.list(query);
        return checkAssemble.toMachineCheckTakeRs(list);
    }

    @Override
    public List<MachineCheckTakeRecord> listRecord(MachineRecordFuzzyQuery query) {
        log.info("query machine check take list record params:{}", query);
        return checkRepository.listRecord(query);
    }

    @Override
    public MachineCheckTakeResult detail(String serialNo) {
        log.info("query machine check take detail params:{}", serialNo);
        MachineCheckTake detail = checkRepository.detail(serialNo);
        return checkAssemble.toMachineCheckTakeR(detail);
    }

    @Override
    public void operationAfterCheckTake(MachineCheckTakeE source) {
        log.info("operationAfterCheckTake params:{}", source);
        Map<String, List<MachineCheckTakeE.Part>> groupByWarehouse = source.getParts().stream().collect(Collectors.groupingBy(MachineCheckTakeE.Part::getOutWarehouseCode));
        groupByWarehouse.forEach((warehouseCode, list) -> {
            BaseWarehouse warehouse = machineHelper.existWarehouseByCode(warehouseCode);
            List<MachineStockTransferVo.MaterialInfo> materialInfoList = new ArrayList<>();
            for (MachineCheckTakeE.Part part : list) {
                MachineStockTransferVo.MaterialInfo material = MachineStockTransferVo.MaterialInfo.builder()
                        .orderSerialNo(part.getOrderSerialNo()).orderDetailId(part.getOrderDetailId())
                        .projectCode(part.getProjectCode()).materialId(part.getMaterialId())
                        .number(part.getTakeNumber()).build();
                materialInfoList.add(material);
            }
//            MachineStockTransferVo stockTransferVo = MachineStockTransferVo.builder().operation(PartOperation.CHECK).outStockWhId(warehouse.getId()).remark("AUTO").sponsor(source.getSponsor()).applicant(source.getTakeBy()).applicantNo(source.getTakeByNo()).materialInfoList(materialInfoList).build();
//            log.info("check transfer params:{}", stockTransferVo);
//            stockManager.transfer(stockTransferVo);
        });
    }

    @Override
    public String sign(MachineSignCmd signCmd) {
        log.info("发送质检取件签名:{}", signCmd);
        MachineCheckTake checkTake = checkRepository.getMachineCheckTake(signCmd.getSerialNo());
        if (checkTake.getStatus() != ProcessStatus.REJECTED && StrUtil.isNotEmpty(checkTake.getSpNo())) {
            throw new ServiceException(StrUtil.format("已发送签字审批，请前往企业微信签字，单号：{}", checkTake.getSpNo()));
        }
        WxApprovalCheckTakeCommitCmd commitCmd = checkAssemble.toCheckTakeApprovalCmd(checkTake);
        log.info("参数：{}", commitCmd);
        R<String> data = wxApprovalService.commitCheckTakeApproval(commitCmd);
        if (data.isFail()) {
            throw new ServiceException(data.getMsg());
        }
        String spNo = data.getMsg();
        checkRepository.sign(checkTake.getSerialNo(), spNo);
        return spNo;
    }

    @Override
    public void signFinish(MachineSignFinishCmd finishCmd) {
        MachineCheckTake checkTake = checkRepository.detail(finishCmd.getSerialNo());
        checkRepository.signFinish(finishCmd);
        if (finishCmd.getStatus() == ProcessStatus.FINISH) {
            // 质检取件零件转移操作
            MachineCheckTakeE machineCheckTakeE = checkAssemble.toCheckTakeE(checkTake);
            log.info("签字完成，开始转移库存：{}", machineCheckTakeE);
            operationAfterCheckTake(machineCheckTakeE);
        }
    }

    public void setApplyInfo(MachineCheckTake check) {
        check.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
        check.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
        check.setTakeTime(LocalDateTime.now());
    }

}
