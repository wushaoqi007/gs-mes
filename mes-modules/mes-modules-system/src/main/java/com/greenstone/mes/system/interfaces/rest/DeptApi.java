package com.greenstone.mes.system.interfaces.rest;

import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.domain.entity.SysDept2;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import com.greenstone.mes.system.domain.service.ISysDeptService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/**
 * 部门信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/dept")
public class DeptApi extends BaseController {
    @Autowired
    private ISysDeptService deptService;

    @GetMapping("/tree/corp/greenstone")
    public AjaxResult gsDepts() {
        List<SysDept2> deptDos = deptService.gsDeptTree();
        return AjaxResult.success(deptDos);
    }

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public AjaxResult list(com.greenstone.mes.system.api.domain.SysDept dept) {
        List<com.greenstone.mes.system.api.domain.SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(depts);
    }

    /**
     * 通过部门全称获取部门信息
     */
    @GetMapping("/info/byFullName")
    public AjaxResult getDeptByName(com.greenstone.mes.system.api.domain.SysDept dept) {
        com.greenstone.mes.system.api.domain.SysDept deptInfo = deptService.selectDeptByName(dept);
        return AjaxResult.success(deptInfo);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<com.greenstone.mes.system.api.domain.SysDept> depts = deptService.selectDeptList(new com.greenstone.mes.system.api.domain.SysDept());
        Iterator<com.greenstone.mes.system.api.domain.SysDept> it = depts.iterator();
        while (it.hasNext()) {
            com.greenstone.mes.system.api.domain.SysDept d = (com.greenstone.mes.system.api.domain.SysDept) it.next();
            if (d.getDeptId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
                it.remove();
            }
        }
        return AjaxResult.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(com.greenstone.mes.system.api.domain.SysDept dept) {
        List<com.greenstone.mes.system.api.domain.SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public AjaxResult roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<com.greenstone.mes.system.api.domain.SysDept> depts = deptService.selectDeptList(new com.greenstone.mes.system.api.domain.SysDept());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.buildDeptTreeSelect(depts));
        return ajax;
    }

    /**
     * 新增部门
     */
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody DeptPo dept) {
        dept.setCreateBy(SecurityUtils.getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody DeptPo dept) {
//        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
//            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
//        } else
        if (dept.getParentId().equals(dept.getDeptId())) {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0) {
            return AjaxResult.error("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return AjaxResult.error("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return AjaxResult.error("部门存在用户,不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }

    @PostMapping("/innerDept")
    public AjaxResult innerDept(@RequestBody DeptPo sysDept) {
        DeptPo dept = deptService.getSysDept(sysDept);
        return AjaxResult.success(dept);
    }
}
