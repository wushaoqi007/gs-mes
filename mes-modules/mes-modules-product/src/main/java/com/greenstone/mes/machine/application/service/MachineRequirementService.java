package com.greenstone.mes.machine.application.service;

import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRequirementQrCodeSaveVO;
import com.greenstone.mes.machine.domain.entity.MachineRequirement;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.table.core.TableService;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;

/**
 * @author wushaoqi
 * @date 2023-11-24-10:01
 */
public interface MachineRequirementService extends TableService<MachineRequirement, MachineRequirementDO, MachineRequirementMapper> {

    void saveRequirementFromMobile(MachineRequirementQrCodeSaveVO qrCodeSaveVO);

    void approval(ApprovalChangeMsg approvalChangeMsg);

    void mailResult(MailSendResult mailSendResult);
}
