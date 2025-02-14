package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.system.application.dto.cmd.PermAddCmd;
import com.greenstone.mes.system.application.dto.cmd.PermImportReq;
import com.greenstone.mes.system.application.dto.cmd.PermMoveCmd;
import com.greenstone.mes.system.application.dto.cmd.RolePermEditCmd;
import com.greenstone.mes.system.application.dto.result.PermTree;
import com.greenstone.mes.system.consts.SysConst;
import com.greenstone.mes.system.domain.converter.SysConverter;
import com.greenstone.mes.system.domain.service.PermService;
import com.greenstone.mes.system.infrastructure.mapper.PermMapper;
import com.greenstone.mes.system.infrastructure.mapper.RolePermMapper;
import com.greenstone.mes.system.infrastructure.po.PermPo;
import com.greenstone.mes.system.infrastructure.po.RolePermPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PermServiceImpl implements PermService {

    private final PermMapper permMapper;

    private final RolePermMapper rolePermMapper;

    private final SysConverter sysConverter;


    @Override
    public List<PermTree> buildPermTree(List<PermPo> permPoList) {
        List<PermTree> permTrees = sysConverter.toPermTrees(permPoList);
        return TreeUtils.toTree(permTrees, SysConst.PERM_ROOT_ID, Comparator.comparingLong(PermTree::getOrderNum));
    }

    @Override
    public PermPo addPerm(PermAddCmd addCmd) {
        // 权限根id为0
        if (addCmd.getParentId() == null) {
            addCmd.setParentId(SysConst.PERM_ROOT_ID);
        }
        // 设置排序
        Long orderNum = permMapper.selectCount(PermPo.builder().parentId(addCmd.getParentId()).build());
        PermPo permPo = sysConverter.toPermPo(addCmd);
        permPo.setOrderNum(orderNum.intValue() + 1);
        // 保存
        permMapper.insert(permPo);
        return permMapper.selectById(permPo.getPermId());
    }

    @Override
    public List<PermPo> list(PermPo perm) {
        return permMapper.list(perm);
    }

    @Override
    public void movePerm(PermMoveCmd moveCmd) {
        log.info("SysMenuServiceImpl.setMenuOrder: start");
        if (moveCmd.getPermId() == null) {
            moveCmd.setPermId(SysConst.PERM_ROOT_ID);
        }
        // 查询字段：id，排序，查询条件：菜单父ID
        LambdaQueryWrapper<PermPo> queryWrapper = Wrappers.lambdaQuery(PermPo.class);
        queryWrapper.eq(PermPo::getParentId, moveCmd.getParentId());
        List<PermPo> childPerms = permMapper.selectList(queryWrapper);
        // 获得排序前的序号
        PermPo targetPerm = childPerms.stream().filter(perm -> moveCmd.getPermId().equals(perm.getPermId())).findFirst().orElse(null);
        if (targetPerm == null) {
            throw new RuntimeException("需要排序的权限不存在");
        }
        // 排序不变，不需要进行操作
        long originOrder = targetPerm.getOrderNum();
        if (originOrder == moveCmd.getOrderNum()) {
            return;
        }
        // 将列表排序
        childPerms.sort(Comparator.comparingInt(PermPo::getOrderNum));
        // 将目标元素移动到指定顺序的地方
        childPerms.remove(targetPerm);
        childPerms.add(moveCmd.getOrderNum() - 1, targetPerm);

        for (int i = 0; i < childPerms.size(); i++) {
            int order = i + 1;
            PermPo permPo = childPerms.get(i);
            if (permPo.getOrderNum() == order) {
                continue;
            }
            LambdaUpdateWrapper<PermPo> updateWrapper = Wrappers.lambdaUpdate(PermPo.class).eq(PermPo::getPermId, permPo.getPermId()).set(PermPo::getOrderNum, order);
            permMapper.update(updateWrapper);
            log.debug("update perm {} {} with order num {}.", permPo.getPermId(), permPo.getPermName(), order);
        }

        log.info("SysMenuServiceImpl.setMenuOrder: end");
    }

    @Override
    public void delete(PermPo perm) {
        permMapper.deleteById(perm);
    }

    @Override
    public void update(PermPo perm) {
        permMapper.updateById(perm);
    }

    @Override
    public List<String> selectPermListByRoleId(Long roleId) {
        List<RolePermPo> rolePerms = rolePermMapper.selectList(Wrappers.lambdaQuery(RolePermPo.class).eq(RolePermPo::getRoleId, roleId));
        return rolePerms.stream().map(RolePermPo::getPermId).toList();
    }

    @Override
    public void updateRolePerm(RolePermEditCmd editCmd) {
        List<RolePermPo> permPos = editCmd.getPermIds().stream().map(permId -> RolePermPo.builder().roleId(editCmd.getRoleId()).permId(permId).build()).toList();
        rolePermMapper.delete(Wrappers.lambdaQuery(RolePermPo.class).eq(RolePermPo::getRoleId, editCmd.getRoleId()));
        rolePermMapper.insertBatchSomeColumn(permPos);
    }

    @Override
    public void importPerms(List<PermImportReq> permImportReqs) {
        PermPo module = null;
        PermPo function = null;
        for (PermImportReq permImportReq : permImportReqs) {
            if (StrUtil.isNotBlank(permImportReq.getModuleCode())) {
                module = permMapper.selectOne(new LambdaQueryWrapper<PermPo>().eq(PermPo::getPermCode, permImportReq.getModuleCode()));
                if (module == null) {
                    module =
                            addPerm(PermAddCmd.builder().permCode(permImportReq.getModuleCode()).permName(permImportReq.getModuleName()).permType(
                                    "M").parentId(SysConst.PERM_ROOT_ID).build());
                }
            }
            if (StrUtil.isNotBlank(permImportReq.getFunctionCode())) {
                if (module == null) {
                    String msg = StrUtil.format("缺少【{}】的上级模块信息，请检查导入内容。", permImportReq.getFunctionName());
                    throw new RuntimeException(msg);
                }
                function = permMapper.selectOne(new LambdaQueryWrapper<PermPo>().eq(PermPo::getPermCode, permImportReq.getFunctionCode()));
                if (function == null) {
                    function =
                            addPerm(PermAddCmd.builder().permCode(permImportReq.getFunctionCode()).permName(permImportReq.getFunctionName())
                                    .permType("F").parentId(module.getPermId()).build());
                }
            }
            if (StrUtil.isNotBlank(permImportReq.getPermCode())) {
                if (function == null) {
                    String msg = StrUtil.format("缺少【{}】的上级功能信息，请检查导入内容。", permImportReq.getFunctionName());
                    throw new RuntimeException(msg);
                }
                PermPo perm = permMapper.selectOne(new LambdaQueryWrapper<PermPo>().eq(PermPo::getPermCode, permImportReq.getPermCode()));
                if (perm == null) {
                    addPerm(PermAddCmd.builder().permCode(permImportReq.getPermCode()).permName(permImportReq.getPermName())
                            .permType("P").parentId(function.getPermId()).build());
                }
            }
        }
    }

    @Override
    public List<String> selectRolePermsByUserId(Long userId) {
        return permMapper.selectRolePermsByUserId(userId);
    }
}
