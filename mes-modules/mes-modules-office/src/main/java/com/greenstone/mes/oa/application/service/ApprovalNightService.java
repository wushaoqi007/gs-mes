package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.ApprovalNightDO;
import com.greenstone.mes.oa.domain.entity.ApprovalNight;

import javax.validation.Valid;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:36
 */
public interface ApprovalNightService extends IServiceWrapper<ApprovalNightDO> {

    void sync(@Valid ApprovalNight approvalNight);

}
