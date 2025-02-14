package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.domain.entity.ApprovalCorrection;

import javax.validation.Valid;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:36
 */
public interface ApprovalCorrectService {

    void sync(@Valid ApprovalCorrection approvalCorrection);

}
