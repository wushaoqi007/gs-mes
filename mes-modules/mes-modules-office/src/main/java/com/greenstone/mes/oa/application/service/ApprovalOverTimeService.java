package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.ApprovalAttendanceDO;
import com.greenstone.mes.oa.domain.entity.ApprovalExtraWork;

import javax.validation.Valid;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:36
 */
public interface ApprovalOverTimeService extends IServiceWrapper<ApprovalAttendanceDO> {

    void sync(@Valid ApprovalExtraWork approvalExtraWork);

}
