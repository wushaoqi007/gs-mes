package com.greenstone.mes.asset.interfaces.rest;

import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairAddCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairEditCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairRemoveCmd;
import com.greenstone.mes.asset.application.dto.cqe.cmd.AssetRepairStatusChangeCmd;
import com.greenstone.mes.asset.application.dto.cqe.query.AssetFuzzyQuery;
import com.greenstone.mes.asset.application.service.AssetRepairService;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/asset/repair")
public class AssetRepairApi extends BaseController {

    private final AssetRepairService assetRepairService;

    @GetMapping("/list")
    public TableDataInfo list(AssetFuzzyQuery query) {
        startPage();
        return getDataTable(assetRepairService.list(query));
    }

    @GetMapping(value = "/{id}")
    public AjaxResult detail(@PathVariable("id") String id) {
        log.info("asset repair detail:{}", id);
        return AjaxResult.success(assetRepairService.detail(id));
    }

    @PostMapping
    public AjaxResult insert(@RequestBody @Validated AssetRepairAddCmd addCmd) {
        log.info("asset repair insert cmd:{}", addCmd);
        assetRepairService.save(addCmd);
        return AjaxResult.success("保存成功");
    }

    @PutMapping
    public AjaxResult update(@RequestBody @Validated AssetRepairEditCmd editCmd) {
        log.info("asset repair update cmd:{}", editCmd);
        assetRepairService.update(editCmd);
        return AjaxResult.success("提交成功");
    }

    @PutMapping("/statusChange")
    public AjaxResult statusChange(@Validated @RequestBody AssetRepairStatusChangeCmd statusChangeCmd) {
        log.info("asset repair status change cmd:{}", statusChangeCmd);
        assetRepairService.statusChange(statusChangeCmd);
        return AjaxResult.success();
    }

    @DeleteMapping
    public AjaxResult remove(@Valid @RequestBody AssetRepairRemoveCmd removeCmd) {
        log.info("asset repair remove cmd:{}", removeCmd);
        assetRepairService.remove(removeCmd.getSerialNos());
        return AjaxResult.success();
    }


}
