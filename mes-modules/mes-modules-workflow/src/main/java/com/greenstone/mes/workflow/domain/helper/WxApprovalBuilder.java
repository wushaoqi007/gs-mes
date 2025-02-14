package com.greenstone.mes.workflow.domain.helper;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.external.enums.TaskStatus;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.infrastructure.persistence.*;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class WxApprovalBuilder {

    public FlwHisTaskPo buildHisTask(FlwInstance flwInstance, SysUser originator, SysUser approver, TaskStatus status,
                                     Long finishTimeStamp) {
        return FlwHisTaskPo.builder().itemId(flwInstance.getItemId())
                .instanceNo(flwInstance.getInstanceNo())
                .serialNo(flwInstance.getSerialNo())
                .processKey(flwInstance.getProcessKey())
                .processName(flwInstance.getProcessName())
                .originatorId(originator.getUserId())
                .originator(originator.getNickName())
                .createTime(0L == finishTimeStamp ? LocalDateTime.now() : LocalDateTimeUtil.of(finishTimeStamp * 1000))
                .originatorNo(originator.getEmployeeNo())
                .approverId(approver.getUserId())
                .approver(approver.getNickName())
                .approverNo(approver.getEmployeeNo())
                .taskStatus(status.getStatus())
                .finishTime(0L == finishTimeStamp ? LocalDateTime.now() : LocalDateTimeUtil.of(finishTimeStamp * 1000)).build();
    }

    public FlwTask buildTask(FlwInstance flwInstance, SysUser originator, SysUser approver) {
        return FlwTask.builder()
                .itemId(flwInstance.getItemId())
                .instanceNo(flwInstance.getInstanceNo())
                .serialNo(flwInstance.getSerialNo())
                .processKey(flwInstance.getProcessKey())
                .processName(flwInstance.getProcessName())
                .originatorId(originator.getUserId())
                .originator(originator.getNickName())
                .originatorNo(originator.getEmployeeNo())
                .createTime(LocalDateTime.now())
                .approverId(approver.getUserId())
                .approver(approver.getNickName())
                .approverNo(approver.getEmployeeNo()).build();
    }

    /**
     * 组建流程实例对象，如果存在serialNo属性则将此属性作为流程实例的serialNo，否则使用响应里的spNo
     *
     * @param originator 发起人
     * @param commitCmd  流程提交信息
     * @param commitResp 流程响应信息
     * @return 流程实例
     */
    public FlwInstance buildInstance(FlwProcess flwProcess, SysUser originator, FlowCommitCmd commitCmd, FlowCommitResp commitResp) {
        FlowCommitCmd.Attr serialNoAttr = commitCmd.getAttrs().stream().filter(attr -> "serialNo".equals(attr.getName())).findFirst().orElse(null);
        FlowCommitCmd.Attr idAttr = commitCmd.getAttrs().stream().filter(attr -> "id".equals(attr.getName())).findFirst().orElse(null);
        if (idAttr == null) {
            throw new RuntimeException("提交审批失败：缺少id信息");
        }

        String serialNo = serialNoAttr == null ? commitResp.getInstanceNo() : serialNoAttr.getValue();
        return FlwInstance.builder().instanceNo(commitResp.getInstanceNo())
                .itemId(Long.valueOf(idAttr.getValue()))
                .originatorId(originator.getUserId())
                .originator(originator.getNickName())
                .originatorNo(originator.getEmployeeNo())
                .serialNo(serialNo)
                .createTime(LocalDateTime.now())
                .wxCpId(originator.getMainWxcpId())
                .processKey(flwProcess.getBusinessKey())
                .processName(flwProcess.getProcessName()).build();
    }

    public FlwHisInstance buildHisInstance(FlwInstance flwInstance, FlwProcess flwProcess, SysUser originator, FlowCommitResp commitResp) {
        return FlwHisInstance.builder().itemId(flwInstance.getItemId())
                .instanceNo(commitResp.getInstanceNo())
                .instanceStatus(ProcessStatus.WAIT_APPROVE.getStatus())
                .originatorId(originator.getUserId())
                .originator(originator.getNickName())
                .originatorNo(originator.getEmployeeNo())
                .createTime(LocalDateTime.now())
                .wxCpId(originator.getMainWxcpId())
                .processKey(flwProcess.getProcessKey())
                .processName(flwProcess.getProcessName()).build();
    }

}
