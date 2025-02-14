package com.greenstone.mes.oa.application.dto.attendance;

import cn.hutool.core.collection.CollectionUtil;
import com.greenstone.mes.oa.infrastructure.util.CheckinUtil;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class ApprovalVector {

    private String spNo;

    private Long start;

    private Long end;

    private String userId;

    /**
     * 审批人
     */
    private String approver;

    public static Periods getPeriodsFromApprovalVector(List<? extends ApprovalVector> approvals) {
        Periods approvalPeriods = new Periods();
        // 如果没有审批单，返回一个空的区间
        if (CollectionUtil.isEmpty(approvals)) {
            return approvalPeriods;
        }
        for (ApprovalVector approval : approvals) {
            // 时间以30分钟为单位计算
            long startTime = CheckinUtil.round(approval.getStart(), TimeUnit.MINUTES.toSeconds(30), TimeUnit.MINUTES.toSeconds(30) - 1);
            long endTime = CheckinUtil.round(approval.getEnd(), TimeUnit.MINUTES.toSeconds(30), 1);
            approvalPeriods.addPeriod(startTime, endTime);
        }
        return approvalPeriods;
    }

}
