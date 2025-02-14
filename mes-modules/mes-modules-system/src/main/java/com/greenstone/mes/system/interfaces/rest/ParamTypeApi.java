package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.system.application.dto.cmd.ParamAddCmd;
import com.greenstone.mes.system.application.dto.query.ParamQuery;
import com.greenstone.mes.system.application.dto.result.ParamResult;
import com.greenstone.mes.system.application.service.ParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:35
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/param/type")
public class ParamTypeApi extends BaseController {

    private final ParamService paramService;

    @GetMapping("/list")
    public TableDataInfo list(ParamQuery query) {
        startPage();
        List<ParamResult> list = paramService.selectParamList(query);
        return getDataTable(list);
    }

    @GetMapping(value = "/info")
    public AjaxResult getByType(ParamQuery query) {
        return AjaxResult.success(paramService.selectParamByType(query.getParamType()));
    }

    /**
     * 新增系统参数
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ParamAddCmd cmd) {
        paramService.saveParam(cmd);
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改系统参数
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ParamAddCmd cmd) {
        paramService.updateParam(cmd);
        return AjaxResult.success("修改成功");
    }

    /**
     * 删除系统参数
     */
    @DeleteMapping("/{paramIds}")
    public AjaxResult remove(@PathVariable String[] paramIds) {
        paramService.deleteParamByIds(paramIds);
        return success();
    }

    /**
     * 刷新缓存
     */
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        paramService.resetParamCache();
        return AjaxResult.success();
    }

}
