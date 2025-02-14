package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.ApprovalVacationDO;
import com.greenstone.mes.oa.domain.entity.ApprovalVacation;

import javax.validation.Valid;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:36
 */
public interface ApprovalLeaveService extends IServiceWrapper<ApprovalVacationDO> {

    void sync(@Valid ApprovalVacation approvalVacation);

}
