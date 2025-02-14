package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.material.cqe.command.WorksheetQrCodeSaveVO;
import com.greenstone.mes.material.cqe.command.WorksheetSaveCommand;
import com.greenstone.mes.material.interfaces.transfer.WorksheetTransfer;
import com.greenstone.mes.material.application.service.WorksheetManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加工单控制类
 *
 * @author gu_renkai
 * @date 2022-08-03
 */
@Slf4j
@RestController
@RequestMapping("/mobile/worksheet")
public class WorksheetMobileController extends BaseController {

    private final WorksheetTransfer worksheetTransfer;
    private final WorksheetManager worksheetManager;

    public WorksheetMobileController(WorksheetTransfer worksheetTransfer, WorksheetManager worksheetManager) {
        this.worksheetTransfer = worksheetTransfer;
        this.worksheetManager = worksheetManager;
    }

    @PostMapping
    public AjaxResult save(@Validated @RequestBody WorksheetQrCodeSaveVO saveVO) {
        WorksheetSaveCommand saveCommand = worksheetTransfer.toSaveCommand(saveVO);
        worksheetManager.saveWorksheet(saveCommand);
        return AjaxResult.success();
    }


}