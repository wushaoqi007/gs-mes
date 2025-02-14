package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.machine.application.assemble.MachineCheckedTakeAssemble;
import com.greenstone.mes.machine.application.assemble.MachineOrderAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineCheckedTakeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRemoveCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineSignCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockTransferVo;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineFuzzyQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderPartScanQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineRecordFuzzyQuery;
import com.greenstone.mes.machine.application.dto.event.MachineCheckedTakeE;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeRecord;
import com.greenstone.mes.machine.application.dto.result.MachineCheckedTakeResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderPartR;
import com.greenstone.mes.machine.application.event.MachineCheckedTakeEvent;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineCheckedTakeService;
import com.greenstone.mes.machine.domain.entity.MachineCheckedTake;
import com.greenstone.mes.machine.domain.entity.MachineCheckedTakeDetail;
import com.greenstone.mes.machine.domain.entity.MachineOrderDetail;
import com.greenstone.mes.machine.domain.repository.MachineCheckedTakeRepository;
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
public class MachineCheckedTakeServiceImpl implements MachineCheckedTakeService {

    private final MachineCheckedTakeRepository checkRepository;
    private final MachineCheckedTakeAssemble checkAssemble;
    private final RemoteSystemService systemService;
    private final ApplicationEventPublisher eventPublisher;
    private final MachineStockManager stockManager;
    private final MachineOrderOldRepository orderRepository;
    private final MachineOrderAssemble orderAssemble;
    private final MachineHelper machineHelper;
    private final RemoteWxApprovalService wxApprovalService;

    @Transactional
    @Override
    public void saveDraft(MachineCheckedTakeAddCmd addCmd) {
        log.info("machine checked take save draft params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheckedTake check = validAndAssembleCheck(addCmd, isNew, false);
        check.setStatus(ProcessStatus.DRAFT);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
    }

    @Transactional
    @Override
    public void saveCommit(MachineCheckedTakeAddCmd addCmd) {
        log.info("machine checked take save commit params:{}", addCmd);
        boolean isNew = addCmd.getId() == null || addCmd.getSerialNo() == null;
        MachineCheckedTake check = validAndAssembleCheck(addCmd, isNew, true);
        check.setStatus(ProcessStatus.COMMITTED);
        if (isNew) {
            checkRepository.add(check);
        } else {
            checkRepository.edit(check);
        }
        // 提交后操作
        eventPublisher.publishEvent(new MachineCheckedTakeEvent(checkAssemble.toCheckedTakeE(check)));
    }

    public MachineCheckedTake validAndAssembleCheck(MachineCheckedTakeAddCmd addCmd, boolean isNew, boolean isCommit) {
        MachineCheckedTake check = checkAssemble.toMachineCheckedTake(addCmd);
        if (isCommit) {
            checkPart(check, addCmd.isForceOperation());
        }
        setApplyInfo(check);
        if (isNew) {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_checked_take").prefix("MCDT" + DateUtil.dateSerialStrNow()).build();
            SerialNoR serialNoR = systemService.getNextSn(nextCmd);
            check.setSerialNo(serialNoR.getSerialNo());
        }
        check.getParts().forEach(p -> p.setSerialNo(check.getSerialNo()));
        return check;
    }

    public void checkPart(MachineCheckedTake check, boolean forceOperation) {
        for (MachineCheckedTakeDetail part : check.getParts()) {
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
        log.info("scan part from machine checked params:{}", query);
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
        MachineOrderPartR machineOrderPartR = orderAssemble.toMachineOrderPartR(orderDetail);
        // 获取库存数量
        machineOrderPartR.setStockNumber(machineHelper.getStockNumber(orderDetail.getMaterialId(), query.getWarehouseCode()));
        return machineOrderPartR;
    }

    @Override
    public SysFile print(String serialNo) {
        MachineCheckedTake detail = checkRepository.detail(serialNo);
        return machineHelper.checkedTakeGenWord(detail);
    }

    @Override
    public void remove(MachineRemoveCmd removeCmd) {
        log.info("MachineRemoveCmd params:{}", removeCmd);
        checkRepository.remove(removeCmd.getSerialNos());
    }

    @Override
    public List<MachineCheckedTakeResult> selectList(MachineFuzzyQuery query) {
        log.info("query machine checked take list params:{}", query);
        List<MachineCheckedTake> list = checkRepository.list(query);
        return checkAssemble.toMachineCheckedTakeRs(list);
    }

    @Override
    public List<MachineCheckedTakeRecord> listRecord(MachineRecordFuzzyQuery query) {
        log.info("query machine checked take list record params:{}", query);
        return checkRepository.listRecord(query);
    }

    @Override
    public MachineCheckedTakeResult detail(String serialNo) {
        log.info("query machine checked take detail params:{}", serialNo);
        MachineCheckedTake detail = checkRepository.detail(serialNo);
        return checkAssemble.toMachineCheckedTakeR(detail);
    }

    @Override
    public void operationAfterCheckedTake(MachineCheckedTakeE source) {
        log.info("operationAfterCheckedTake params:{}", source);
        Map<String, List<MachineCheckedTakeE.Part>> groupByWarehouse = source.getParts().stream().collect(Collectors.groupingBy(MachineCheckedTakeE.Part::getOutWarehouseCode));
        groupByWarehouse.forEach((warehouseCode, list) -> {
            BaseWarehouse warehouse = machineHelper.existWarehouseByCode(warehouseCode);
            List<MachineStockTransferVo.MaterialInfo> materialInfoList = new ArrayList<>();
            for (MachineCheckedTakeE.Part part : list) {
                MachineStockTransferVo.MaterialInfo material = MachineStockTransferVo.MaterialInfo.builder()
                        .orderSerialNo(part.getOrderSerialNo()).orderDetailId(part.getOrderDetailId())
                        .projectCode(part.getProjectCode()).materialId(part.getMaterialId())
                        .number(part.getTakeNumber()).build();
                materialInfoList.add(material);
            }
//            MachineStockTransferVo stockTransferVo = MachineStockTransferVo.builder().operation(PartOperation.CHECKED_TAKE).outStockWhId(warehouse.getId()).remark("AUTO").sponsor(source.getSponsor()).applicant(source.getTakeBy()).applicantNo(source.getTakeByNo()).materialInfoList(materialInfoList).build();
//            log.info("checked transfer params:{}", stockTransferVo);
//            stockManager.transfer(stockTransferVo);
        });
    }

    @Override
    public String sign(MachineSignCmd signCmd) {
        log.info("发送合格品取件签名:{}", signCmd);
        MachineCheckedTake checkTake = checkRepository.getMachineCheckedTake(signCmd.getSerialNo());
        if (checkTake.getStatus() != ProcessStatus.REJECTED && StrUtil.isNotEmpty(checkTake.getSpNo())) {
            throw new ServiceException(StrUtil.format("已发送签字审批，请前往企业微信签字，单号：{}", checkTake.getSpNo()));
        }
        WxApprovalCheckedTakeCommitCmd commitCmd = checkAssemble.toCheckedTakeApprovalCmd(checkTake);
        log.info("参数：{}", commitCmd);
        R<String> data = wxApprovalService.commitCheckedTakeApproval(commitCmd);
        if (data.isFail()) {
            throw new ServiceException(data.getMsg());
        }
        String spNo = data.getMsg();
        checkRepository.sign(checkTake.getSerialNo(), spNo);
        return spNo;
    }

    @Override
    public void signFinish(MachineSignFinishCmd finishCmd) {
        MachineCheckedTake checkTake = checkRepository.detail(finishCmd.getSerialNo());
        checkRepository.signFinish(finishCmd);
        if (finishCmd.getStatus() == ProcessStatus.FINISH) {
            MachineCheckedTakeE machineCheckedTakeE = checkAssemble.toCheckedTakeE(checkTake);
            // 合格品取件零件转移操作
            log.info("签字完成，开始转移库存：{}", machineCheckedTakeE);
            operationAfterCheckedTake(machineCheckedTakeE);
        }
    }

    public void setApplyInfo(MachineCheckedTake check) {
        check.setSponsorId(SecurityUtils.getLoginUser().getUser().getUserId());
        check.setSponsor(SecurityUtils.getLoginUser().getUser().getNickName());
        check.setTakeTime(LocalDateTime.now());
    }

}
