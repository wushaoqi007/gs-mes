package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.base.api.RemoteMachineService;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import com.greenstone.mes.machine.enums.MachineType;
import com.greenstone.mes.oa.application.assembler.ApprovalAssembler;
import com.greenstone.mes.oa.application.dto.ApproVacationImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalCorrectionImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalExtraWorkImportDTO;
import com.greenstone.mes.oa.application.dto.ApprovalNightImportDTO;
import com.greenstone.mes.oa.application.helper.ApprovalHelper;
import com.greenstone.mes.oa.application.service.ApprovalService;
import com.greenstone.mes.oa.application.service.AttendanceService;
import com.greenstone.mes.oa.application.service.OaWxFileService;
import com.greenstone.mes.oa.application.service.WxCheckinDataService;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.interfaces.request.ApprovalCorrectionImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalExtraWorkImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalNightImportCommand;
import com.greenstone.mes.oa.interfaces.request.ApprovalVacationImportCommand;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final WxUserService externalWxUserService;
    private final WxOaService externalWxOaService;
    private final WxApprovalRepository wxApprovalRepository;
    private final OaWxFileService oaWxFileService;
    private final AttendanceService attendanceService;
    private final ApprovalHelper approvalHelper;
    private final ApprovalAssembler approvalAssembler;
    private final WxCheckinDataService wxCheckinDataService;
    private final RemoteUserService userService;
    private final RemoteMachineService machineService;


    @Override
    @Transactional
    public void sync(CpId cpId, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        log.info("Sync approval type: {}, no: {}", approvalDetail.getSpName(), approvalDetail.getSpNo());
        WxCpUser user = externalWxUserService.getUserInSilent(cpId, new WxUserId(approvalDetail.getApplier().getUserId()));
        if (user == null) {
            log.info("user:{} not found in sync approval ", approvalDetail.getApplier().getUserId());
            return;
        }
        // 审批类型名称
        String spName = approvalDetail.getSpName();
        switch (spName) {
            case "加班" -> sync(ApprovalExtraWork.from(cpId, user.getName(), approvalDetail));
            case "请假" -> sync(ApprovalVacation.from(cpId, user.getName(), approvalDetail));
            case "打卡补卡" -> sync(ApprovalCorrection.from(cpId, user.getName(), approvalDetail));
            case "夜班" -> sync(ApprovalNight.from(cpId, user.getName(), approvalDetail));
            case "临时变更" -> sync(ApprovalTemporaryChange.from(cpId, user.getName(), approvalDetail));
            case "质检取件签字" -> sync(ApprovalCheckTake.from(cpId, user.getName(), approvalDetail));
            case "合格品取件签字" -> sync(ApprovalCheckedTake.from(cpId, user.getName(), approvalDetail));
            case "出库签字" -> sync(ApprovalFinish.from(cpId, user.getName(), approvalDetail));
            default -> log.error("Unsupported approval name: {}", spName);
        }
    }

    @Override
    @Async
    public void importVacations(CpId cpId, List<ApprovalVacationImportCommand> commands) {
        List<ApproVacationImportDTO> dtos = approvalAssembler.toVacationImportDTOs(cpId, commands);
        for (ApproVacationImportDTO dto : dtos) {
            if (dto.hasMediaFile()) {
                WxCpApprovalDetailResult approvalDetail = externalWxOaService.getApprovalDetail(dto.getCpId(), dto.getSpNo());
                ApprovalVacation approval = ApprovalVacation.from(dto.getCpId(), dto.getUserName(), approvalDetail.getInfo());
                wxApprovalRepository.saveVacation(approval);
                oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
            } else {
                ApprovalVacation approvalVacation = approvalAssembler.toVacation(dto);
                wxApprovalRepository.saveVacation(approvalVacation);
            }
        }
    }

    @Override
    @Async
    public void importNights(CpId cpId, List<ApprovalNightImportCommand> commands) {
        List<ApprovalNightImportDTO> dtos = approvalAssembler.toNightImportDTOs(cpId, commands);
        for (ApprovalNightImportDTO dto : dtos) {
            ApprovalNight approvalNight = approvalAssembler.toNight(dto);
            wxApprovalRepository.saveNight(approvalNight);
        }
    }

    @Override
    @Async
    public void importExtraWorks(CpId cpId, List<ApprovalExtraWorkImportCommand> commands) {
        List<ApprovalExtraWorkImportDTO> dtos = approvalAssembler.toExtraWorkImportDTOs(cpId, commands);
        for (ApprovalExtraWorkImportDTO dto : dtos) {
            ApprovalExtraWork approvalExtraWork = approvalAssembler.toExtraWork(dto);
            wxApprovalRepository.saveExtraWork(approvalExtraWork);
        }
    }

    @Override
    @Async
    public void importCorrections(CpId cpId, List<ApprovalCorrectionImportCommand> commands) {
        List<ApprovalCorrectionImportDTO> dtos = approvalAssembler.toCorrectionImportDTOs(cpId, commands);
        for (ApprovalCorrectionImportDTO dto : dtos) {
            if (dto.hasMediaFile()) {
                WxCpApprovalDetailResult approvalDetail = externalWxOaService.getApprovalDetail(dto.getCpId(), dto.getSpNo());
                ApprovalCorrection approval = ApprovalCorrection.from(dto.getCpId(), dto.getUserName(), approvalDetail.getInfo());
                wxApprovalRepository.saveCorrection(approval);
                oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
            } else {
                ApprovalCorrection approvalCorrection = approvalAssembler.toCorrection(dto);
                wxApprovalRepository.saveCorrection(approvalCorrection);
            }
        }
    }

    @Override
    public List<String> listApprovalOfAuditing(Date startDate, Date endDate, String cpId) {
        return wxApprovalRepository.listApprovalOfAuditing(startDate, endDate, cpId);
    }

    private void sync(ApprovalCorrection approval) {
        boolean isUpdateOrSave = wxApprovalRepository.saveCorrection(approval);
        oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
        if (isUpdateOrSave) {
            // 更新打卡数据
            SyncCheckinDataCmd syncCheckinDataCmd = SyncCheckinDataCmd.builder().cpId(approval.getCpId().id()).wxUserId(approval.getUserId().id())
                    .startDate(DateUtil.beginOfDay(approval.getCorrectionTime())).endDate(DateUtil.endOfDay(approval.getCorrectionTime())).build();
            wxCheckinDataService.syncCheckData(syncCheckinDataCmd);
            attendanceService.calcAndSave(approval.getCorrectionTime(), approval.getCorrectionTime(), approval.getCpId(), approval.getUserId());
        }
    }

    private void sync(ApprovalVacation approval) {
        boolean isUpdateOrSave = wxApprovalRepository.saveVacation(approval);
        oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
        if (isUpdateOrSave) {
            attendanceService.calcAndSave(approval.getStartTime(), approval.getEndTime(), approval.getCpId(), approval.getUserId());
        }
    }

    private void sync(ApprovalExtraWork approval) {
        boolean isUpdateOrSave = wxApprovalRepository.saveExtraWork(approval);
        oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
        if (isUpdateOrSave) {
            attendanceService.calcAndSave(approval.getStartTime(), approval.getEndTime(), approval.getCpId(), approval.getUserId());
        }
    }

    private void sync(ApprovalNight approval) {
        boolean isUpdateOrSave = wxApprovalRepository.saveNight(approval);
        oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
        if (isUpdateOrSave) {
            attendanceService.calcAndSave(approval.getStartTime(), approval.getEndTime(), approval.getCpId(), approval.getUserId());
        }
        approvalHelper.syncNightShift(approval);
    }

    private void sync(ApprovalTemporaryChange approval) {
        boolean isUpdateOrSave = wxApprovalRepository.saveTemporaryChange(approval);
        oaWxFileService.saveOrUpdateFile(approval.getCpId(), approval.getSpNo(), approval.allMedias());
        if (isUpdateOrSave) {
            attendanceService.calcAndSave(approval.getStartTime(), approval.getEndTime(), approval.getCpId(), approval.getUserId());
        }
        approvalHelper.syncDayShift(approval);
    }

    private void sync(ApprovalCheckTake approval) {
        log.info("收到质检取件回调：{}", approval);
        if (approval.getStatus() == ApprovalStatus.PASSED) {
            machineService.checkTakeSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.FINISH).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
        if (approval.getStatus() == ApprovalStatus.REJECTED) {
            machineService.checkTakeSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.REJECTED).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
    }

    private void sync(ApprovalCheckedTake approval) {
        log.info("收到合格品取件回调：{}", approval);
        if (approval.getStatus() == ApprovalStatus.PASSED) {
            machineService.checkedTakeSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.FINISH).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
        if (approval.getStatus() == ApprovalStatus.REJECTED) {
            machineService.checkedTakeSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.REJECTED).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
    }

    private void sync(ApprovalFinish approval) {
        log.info("收到出库回调：{}", approval);
        if (approval.getStatus() == ApprovalStatus.PASSED) {
            machineService.warehouseOutSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.FINISH).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
        if (approval.getStatus() == ApprovalStatus.REJECTED) {
            machineService.warehouseOutSignFinish(MachineSignFinishCmd.builder().status(ProcessStatus.REJECTED).serialNo(approval.getSerialNo()).spNo(approval.getSpNo().no()).machineType(MachineType.CHECK_TAKE).build());
        }
    }

}
