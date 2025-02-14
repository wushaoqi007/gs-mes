package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.system.domain.SysCustomTable;
import com.greenstone.mes.system.dto.cmd.SysCustomTableAddReq;
import com.greenstone.mes.system.dto.query.SysCustomTableListReq;
import com.greenstone.mes.system.domain.service.ISysCustomTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统自定义接口
 *
 * @author wushaoqi
 * @date 2022-10-31-8:42
 */
@Slf4j
@RestController
@RequestMapping("/custom")
public class SysCustomController extends BaseController {

    @Autowired
    private ISysCustomTableService customTableService;

    /**
     * 新增自定义列
     */
    @Log(title = "自定义列", businessType = BusinessType.INSERT)
    @PostMapping("/table")
    public AjaxResult addTable(@Validated @RequestBody SysCustomTableAddReq customTableAddReq) {
        customTableService.addSysCustomTable(customTableAddReq);
        return AjaxResult.success("新增成功");
    }

    /**
     * 获取自定义列列表
     */
    @GetMapping("/table/list")
    public AjaxResult list(@Validated SysCustomTableListReq customTableListReq) {
        List<SysCustomTable> list = customTableService.selectCustomTableList(customTableListReq);
        return AjaxResult.success(list);
    }

    /**
     * 还原自定义列
     */
    @Log(title = "自定义列", businessType = BusinessType.INSERT)
    @PutMapping("/table/reset")
    public AjaxResult resetTable(@Validated @RequestBody SysCustomTableListReq customTableListReq) {
        customTableService.resetSysCustomTable(customTableListReq);
        return AjaxResult.success("还原成功");
    }

}
