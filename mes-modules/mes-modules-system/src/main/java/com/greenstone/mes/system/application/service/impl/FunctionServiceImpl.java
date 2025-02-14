package com.greenstone.mes.system.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.application.assembler.FunctionAssembler;
import com.greenstone.mes.system.application.dto.cmd.FunctionAddCmd;
import com.greenstone.mes.system.application.dto.cmd.FunctionMoveCmd;
import com.greenstone.mes.system.application.dto.result.FunctionPermissionResult;
import com.greenstone.mes.system.application.service.FunctionService;
import com.greenstone.mes.system.domain.converter.FunctionConverter;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.domain.entity.Navigation;
import com.greenstone.mes.system.domain.repository.*;
import com.greenstone.mes.system.dto.result.FunctionResult;
import com.greenstone.mes.system.infrastructure.enums.FunctionType;
import com.greenstone.mes.system.infrastructure.mapper.FunctionMapper;
import com.greenstone.mes.system.infrastructure.po.FunctionPermissionDO;
import com.greenstone.mes.system.infrastructure.po.PermissionGroupTempDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2024-10-18-8:38
 */
@AllArgsConstructor
@Slf4j
@Service
public class FunctionServiceImpl implements FunctionService {
    private final FunctionRepository functionRepository;
    private final FunctionAssembler assembler;
    private final NavigationRepository navigationRepository;
    private final FunctionPermissionRepository functionPermissionRepository;
    private final MemberPermissionRepository memberPermissionRepository;
    private final MemberNavigationRepository memberNavigationRepository;
    private final PermissionGroupTempRepository permissionGroupTempRepository;
    private final FunctionMapper functionMapper;
    private final FunctionConverter converter;

    @Transactional
    @Override
    public void saveFunction(FunctionAddCmd addCmd) {
        addCmd.validate();
        Function function = assembler.toFunction(addCmd);
        function.autoCompleteCreateData();
        Long count = functionRepository.selectCount();
        function.setOrderNum(count.intValue());
        functionRepository.saveFunction(function);
        // 新增功能权限组
        List<PermissionGroupTempDO> permissionGroupTemps = permissionGroupTempRepository.listAll();
        List<PermissionGroupTempDO> pagePermissionGroupTemp = permissionGroupTemps.stream().filter(PermissionGroupTempDO::getPagePermission).toList();
        List<PermissionGroupTempDO> tablePermissionGroupTemp = permissionGroupTemps.stream().filter(p -> !p.getPagePermission()).toList();
        List<FunctionPermissionDO> insertFunctionPermissions = new ArrayList<>();
        if (function.getType().equals(FunctionType.PAGE.getType())) {
            for (PermissionGroupTempDO permissionGroupTemp : pagePermissionGroupTemp) {
                insertFunctionPermissions.add(FunctionPermissionDO.builder().functionId(function.getId())
                        .permissionGroupName(permissionGroupTemp.getTypeName())
                        .permissionGroupTypeName(permissionGroupTemp.getTypeName())
                        .rights(permissionGroupTemp.getRights())
                        .viewFilter(permissionGroupTemp.getViewFilter())
                        .updateFilter(permissionGroupTemp.getUpdateFilter()).build());
            }
        } else {
            for (PermissionGroupTempDO permissionGroupTemp : tablePermissionGroupTemp) {
                insertFunctionPermissions.add(FunctionPermissionDO.builder().functionId(function.getId())
                        .permissionGroupName(permissionGroupTemp.getTypeName())
                        .permissionGroupTypeName(permissionGroupTemp.getTypeName())
                        .rights(permissionGroupTemp.getRights())
                        .viewFilter(permissionGroupTemp.getViewFilter())
                        .updateFilter(permissionGroupTemp.getUpdateFilter()).build());
            }
        }
        functionPermissionRepository.addFunctionPermissions(insertFunctionPermissions);
    }

    @Transactional
    @Override
    public void updateFunction(FunctionAddCmd updateCmd) {
        if (updateCmd.getId() == null) {
            throw new ServiceException("编辑功能，id不能为空");
        }
        Function find = functionRepository.selectById(updateCmd.getId());
        if (find == null) {
            throw new ServiceException(StrUtil.format("编辑的功能未找到，id：{}", updateCmd.getId()));
        }
        updateCmd.validate();
        Function function = assembler.toFunction(updateCmd);
        function.autoCompleteUpdateData();
        functionRepository.updateFunction(function);
    }

    @Transactional
    @Override
    public void removeFunction(Long id) {
        Function function = functionRepository.selectById(id);
        if (function == null) {
            throw new ServiceException(StrUtil.format("功能未找到，id:{}", id));
        }
        // 删除功能时，删除功能的导航和功能的权限
        List<Navigation> existNavigations = navigationRepository.selectByFunctionId(id);
        if (CollUtil.isNotEmpty(existNavigations)) {
            // 删除功能配置的导航
            navigationRepository.removeNavigationByFunctionId(id);
            // 删除成员的导航权限
            List<Long> navigationIds = existNavigations.stream().map(Navigation::getId).toList();
            memberNavigationRepository.deleteByNavigationId(navigationIds);
        }
        // 删除功能的权限
        // 功能有哪些权限组，这些权限组对应的成员权限需要删除
        List<FunctionPermissionDO> functionPermissions = functionPermissionRepository.list(id);
        if (CollUtil.isNotEmpty(functionPermissions)) {
            List<Long> functionPermIds = functionPermissions.stream().map(FunctionPermissionDO::getId).toList();
            memberPermissionRepository.deleteMemberPermissionByFunPermId(functionPermIds);
        }
        // 删除功能的权限组
        functionPermissionRepository.deleteByFunctionId(id);
        // 删除功能
        functionRepository.removeFunction(id);
    }

    @Override
    public List<FunctionResult> listAll() {
        List<Function> functions = functionRepository.listAll();
        return assembler.toFunctionRs(functions);
    }

    @Override
    public List<Function> getFunctions() {
        return converter.dos2Entities(functionMapper.selectList(null));
    }

    @Override
    public FunctionResult detail(Long id) {
        Function function = functionRepository.selectById(id);
        if (function == null) {
            throw new ServiceException(StrUtil.format("功能未找到，id:{}", id));
        }
        return assembler.toFunctionR(function);
    }

    @Override
    public void moveFunction(FunctionMoveCmd sortCmd) {
        log.info("Function move: start");
        List<Function> functions = functionRepository.listAll();
        List<Function> sortFunctions = functions.stream().filter(f -> !f.getId().equals(sortCmd.getId())).sorted(Comparator.comparing(Function::getOrderNum)).toList();
        for (int i = 0; i < sortFunctions.size(); i++) {
            Function function = sortFunctions.get(i);
            int order = sortCmd.getOrderNum() > i ? i : i + 1;
            if (order != function.getOrderNum()) {
                // 在这之后的菜单排序需要 +1
                functionRepository.updateSort(function.getId(), order);
                log.debug("function {} {} update with order num {}.", function.getId(), function.getId(), order);
            } else {
                log.debug("function {} {} no need update", function.getId(), function.getId());
            }
        }
        functionRepository.updateSort(sortCmd.getId(), sortCmd.getOrderNum());
        log.info("Function move: end");
    }

    @Override
    public List<FunctionPermissionResult> listAllFunctionWithPerm() {
        List<Function> functions = functionRepository.listAll();
        List<FunctionPermissionDO> functionPermission = functionPermissionRepository.listAll();
        List<FunctionPermissionResult> results = new ArrayList<>();
        for (Function function : functions) {
            List<FunctionPermissionDO> perms = functionPermission.stream().filter(p -> Objects.equals(p.getFunctionId(), function.getId())).collect(Collectors.toList());
            List<FunctionPermissionResult.PermissionGroup> permGroups = assembler.toPermGroups(perms);
            results.add(FunctionPermissionResult.builder()
                    .functionId(function.getId())
                    .functionName(function.getName())
                    .functionType(function.getType())
                    .permissionGroups(permGroups).build());
        }
        return results;
    }
}
