package com.greenstone.mes.machine.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.machine.application.assemble.MachineRequirementAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRequirementQrCodeSaveVO;
import com.greenstone.mes.machine.application.service.MachineRequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wushaoqi
 * @date 2023-11-24-9:59
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/requirement")
public class MachineRequirementMobileApi extends BaseController {

    private final MachineRequirementService requirementService;

    @PostMapping
    public AjaxResult save(@Validated @RequestBody MachineRequirementQrCodeSaveVO qrCodeSaveVO) {
        log.info("mobile requirement save params:{}", qrCodeSaveVO);
        requirementService.saveRequirementFromMobile(qrCodeSaveVO);
        return AjaxResult.success();
    }
}
