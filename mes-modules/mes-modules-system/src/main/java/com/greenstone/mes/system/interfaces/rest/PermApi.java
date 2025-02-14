package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.ApiLog;
import com.greenstone.mes.common.security.annotation.RequiresRoles;
import com.greenstone.mes.system.application.dto.cmd.PermAddCmd;
import com.greenstone.mes.system.application.dto.cmd.PermImportReq;
import com.greenstone.mes.system.application.dto.cmd.PermMoveCmd;
import com.greenstone.mes.system.application.dto.cmd.RolePermEditCmd;
import com.greenstone.mes.system.application.dto.result.PermTree;
import com.greenstone.mes.system.domain.service.PermService;
import com.greenstone.mes.system.infrastructure.po.PermPo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/perm")
public class PermApi extends BaseController {

    private final PermService permService;

    @RequiresRoles("admin")
    @GetMapping("/tree")
    public AjaxResult permTree() {
        List<PermPo> permPos = permService.list(null);
        List<PermTree> permTrees = permService.buildPermTree(permPos);
        return AjaxResult.success("查询成功", permTrees);
    }

    @RequiresRoles("admin")
    @PostMapping
    @ApiLog
    public AjaxResult addPerm(@RequestBody @Validated PermAddCmd addCmd) {
        permService.addPerm(addCmd);
        return AjaxResult.success("新增权限成功");
    }

    @RequiresRoles("admin")
    @PutMapping("/move")
    @ApiLog
    public AjaxResult movePerm(@RequestBody @Validated PermMoveCmd moveCmd) {
        permService.movePerm(moveCmd);
        return AjaxResult.success();
    }

    @RequiresRoles("admin")
    @PutMapping
    @ApiLog
    public AjaxResult update(@RequestBody PermPo perm) {
        permService.update(perm);
        return AjaxResult.success("更新权限成功");
    }

    @RequiresRoles("admin")
    @DeleteMapping
    @ApiLog
    public AjaxResult delete(@RequestBody PermPo perm) {
        permService.delete(perm);
        return AjaxResult.success("删除权限成功");
    }

    @RequiresRoles("admin")
    @GetMapping("/rolePerm/{roleId}")
    @ApiLog
    public AjaxResult rolePermTree(@PathVariable("roleId") Long roleId) {
        AjaxResult ajax = AjaxResult.success();
        List<PermPo> permPos = permService.list(null);
        ajax.put("checkedKeys", permService.selectPermListByRoleId(roleId));
        ajax.put("perms", permService.buildPermTree(permPos));
        return ajax;
    }

    @RequiresRoles("admin")
    @PutMapping("/rolePerm")
    @ApiLog
    public AjaxResult updateRolePerm(@RequestBody @Validated RolePermEditCmd rolePermEditCmd) {
        permService.updateRolePerm(rolePermEditCmd);
        return AjaxResult.success();
    }

    @Transactional
    @ApiLog
    @RequiresRoles("admin")
    @PostMapping("/import")
    public AjaxResult importPerm(MultipartFile file) {
        ExcelUtil<PermImportReq> excelUtil = new ExcelUtil<>(PermImportReq.class);
        List<PermImportReq> permImportReqs = excelUtil.toList(file);
        permService.importPerms(permImportReqs);
        return AjaxResult.success("导入成功");
    }

}