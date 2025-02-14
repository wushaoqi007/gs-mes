package com.greenstone.mes.system.domain.service.impl;

import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.text.Convert;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.common.datascope.annotation.DataScopeOld;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.domain.SysRole;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.converter.DeptConverter;
import com.greenstone.mes.system.domain.entity.SysDept2;
import com.greenstone.mes.system.domain.service.ISysDeptService;
import com.greenstone.mes.system.domain.vo.TreeSelect;
import com.greenstone.mes.system.infrastructure.constant.WorkwxConst;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import com.greenstone.mes.system.infrastructure.mapper.SysDeptMapper;
import com.greenstone.mes.system.infrastructure.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService {
    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private DeptConverter deptConverter;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<com.greenstone.mes.system.api.domain.SysDept> selectDeptList(com.greenstone.mes.system.api.domain.SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    @Override
    public List<DeptPo> gsDepts() {
        DeptPo dept = DeptPo.builder().cpId(WorkwxConst.CpId.GREENSTONE).build();
        return deptMapper.list(dept);
    }

    @Override
    public List<SysDept2> gsDeptTree() {
        List<DeptPo> deptDos = gsDepts();
        List<SysDept2> depts = deptConverter.do2Entities(deptDos);
        return TreeUtils.toTree(depts);
    }

    @Override
    public com.greenstone.mes.system.api.domain.SysDept selectDeptByName(com.greenstone.mes.system.api.domain.SysDept dept) {
        return deptMapper.selectDeptByName(dept);
    }

    @Override
    public DeptPo getSysDept(DeptPo dept) {
        return deptMapper.getOneOnly(dept);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<com.greenstone.mes.system.api.domain.SysDept> buildDeptTree(List<com.greenstone.mes.system.api.domain.SysDept> depts) {
        List<com.greenstone.mes.system.api.domain.SysDept> returnList = new ArrayList<com.greenstone.mes.system.api.domain.SysDept>();
        List<Long> tempList = new ArrayList<Long>();
        for (com.greenstone.mes.system.api.domain.SysDept dept : depts) {
            tempList.add(dept.getDeptId());
        }
        for (Iterator<com.greenstone.mes.system.api.domain.SysDept> iterator = depts.iterator(); iterator.hasNext(); ) {
            com.greenstone.mes.system.api.domain.SysDept dept = (com.greenstone.mes.system.api.domain.SysDept) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<com.greenstone.mes.system.api.domain.SysDept> depts) {
        List<com.greenstone.mes.system.api.domain.SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public DeptPo selectDeptById(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(com.greenstone.mes.system.api.domain.SysDept dept) {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        com.greenstone.mes.system.api.domain.SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            com.greenstone.mes.system.api.domain.SysDept dept = new com.greenstone.mes.system.api.domain.SysDept();
            dept.setDeptId(deptId);
            List<com.greenstone.mes.system.api.domain.SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts)) {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(DeptPo dept) {
        DeptPo info = deptMapper.selectById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (info != null && !UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        if (info != null && StringUtils.isNotBlank(info.getAncestors())) {
            dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        }

        return deptMapper.insert(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(DeptPo dept) {
        DeptPo newParentDept = deptMapper.selectById(dept.getParentId());
        DeptPo oldDept = deptMapper.selectById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(DeptPo dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<com.greenstone.mes.system.api.domain.SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (com.greenstone.mes.system.api.domain.SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<com.greenstone.mes.system.api.domain.SysDept> list, com.greenstone.mes.system.api.domain.SysDept t) {
        // 得到子节点列表
        List<com.greenstone.mes.system.api.domain.SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (com.greenstone.mes.system.api.domain.SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<com.greenstone.mes.system.api.domain.SysDept> getChildList(List<com.greenstone.mes.system.api.domain.SysDept> list, com.greenstone.mes.system.api.domain.SysDept t) {
        List<com.greenstone.mes.system.api.domain.SysDept> tlist = new ArrayList<com.greenstone.mes.system.api.domain.SysDept>();
        Iterator<com.greenstone.mes.system.api.domain.SysDept> it = list.iterator();
        while (it.hasNext()) {
            com.greenstone.mes.system.api.domain.SysDept n = (com.greenstone.mes.system.api.domain.SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<com.greenstone.mes.system.api.domain.SysDept> list, com.greenstone.mes.system.api.domain.SysDept t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}
