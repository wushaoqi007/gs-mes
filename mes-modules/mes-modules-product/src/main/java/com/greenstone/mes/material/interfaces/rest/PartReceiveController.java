package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.material.domain.service.PartReceiveService;
import com.greenstone.mes.material.dto.cmd.PartReceiveListQuery;
import com.greenstone.mes.material.dto.cmd.PartReceiveRecordListQuery;
import com.greenstone.mes.material.interfaces.response.PartReceiveR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 取件记录控制类
 */
@Slf4j
@RestController
@RequestMapping("/part/receive")
public class PartReceiveController extends BaseController {

    private final PartReceiveService partReceiveService;

    public PartReceiveController(PartReceiveService partReceiveService) {
        this.partReceiveService = partReceiveService;
    }

    @GetMapping("/record/list")
    public TableDataInfo recordList(PartReceiveRecordListQuery query) {
        startPage();
        return getDataTable(partReceiveService.recordList(query));
    }

    @GetMapping("/list")
    public AjaxResult partList(PartReceiveListQuery query) {
        List<PartReceiveR> list = partReceiveService.listPartsByRecordId(query);
        return AjaxResult.success(list);
    }
}