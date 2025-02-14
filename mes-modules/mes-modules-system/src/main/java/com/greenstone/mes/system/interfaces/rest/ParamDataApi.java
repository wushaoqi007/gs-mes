package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.system.application.dto.cmd.ParamDataAddCmd;
import com.greenstone.mes.system.application.dto.cmd.ParamMoveCmd;
import com.greenstone.mes.system.application.dto.query.ParamDataQuery;
import com.greenstone.mes.system.application.service.ParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wushaoqi
 * @date 2024-03-12-15:35
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/param/data")
public class ParamDataApi extends BaseController {

    private final ParamService paramService;

    @PutMapping("/move")
    @ApiLog
    @Transactional
    public AjaxResult sort(@RequestBody ParamMoveCmd sortCmd) {
        paramService.moveParamData(sortCmd);
        return AjaxResult.success("排序完成");
    }

    /**
     * 查询系统参数详细
     */
    @GetMapping(value = "/list")
    public AjaxResult list(@Validated ParamDataQuery query) {
        return AjaxResult.success(paramService.selectParamDataList(query));
    }

    /**
     * 新增系统参数详情
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ParamDataAddCmd cmd) {
        paramService.saveData(cmd);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改系统参数详情
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ParamDataAddCmd cmd) {
        paramService.updateData(cmd);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping("/{dataIds}")
    public AjaxResult remove(@PathVariable String[] dataIds) {
        paramService.deleteParamDataByIds(dataIds);
        return success();
    }
}
