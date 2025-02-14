package com.greenstone.mes.system.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.TreeUtils;
import com.greenstone.mes.system.application.assembler.NavigationAssembler;
import com.greenstone.mes.system.application.dto.cmd.NavigationAddCmd;
import com.greenstone.mes.system.application.dto.cmd.NavigationMoveCmd;
import com.greenstone.mes.system.application.dto.result.NavigationResult;
import com.greenstone.mes.system.application.dto.result.NavigationSelectResult;
import com.greenstone.mes.system.application.dto.result.NavigationTree;
import com.greenstone.mes.system.application.service.NavigationService;
import com.greenstone.mes.system.consts.SysConst;
import com.greenstone.mes.system.domain.entity.Navigation;
import com.greenstone.mes.system.domain.repository.*;
import com.greenstone.mes.system.infrastructure.enums.NavigationCategory;
import com.greenstone.mes.system.infrastructure.enums.NavigationType;
import com.greenstone.mes.system.infrastructure.po.FunctionPermissionDO;
import com.greenstone.mes.system.infrastructure.po.MemberNavigationDO;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2024-10-18-11:07
 */
@AllArgsConstructor
@Slf4j
@Service
public class NavigationServiceImpl implements NavigationService {
    private final NavigationRepository navigationRepository;
    private final NavigationAssembler assembler;
    private final FunctionRepository functionRepository;
    private final MemberNavigationRepository memberNavigationRepository;
    private final MemberPermissionRepository memberPermissionRepository;
    private final FunctionPermissionRepository functionPermissionRepository;

    @Transactional
    @Override
    public void saveNavigation(NavigationAddCmd addCmd) {
        addCmd.validate();
        Navigation navigation = assembler.toNavigation(addCmd);
        if (navigation.getParentId() == null) {
            navigation.setParentId(SysConst.NAVIGATION_ROOT_ID);
        }
        validateParent(navigation, navigation.getParentId());
        if (navigation.getFunctionId() != null) {
            functionRepository.exist(navigation.getFunctionId());
        }
        navigation.autoCompleteCreateData();
        Long count = navigationRepository.selectCount(navigation.getParentId());
        navigation.setOrderNum(count.intValue());
        navigationRepository.saveNavigation(navigation);
    }

    @Transactional
    @Override
    public void updateNavigation(NavigationAddCmd updateCmd) {
        if (updateCmd.getId() == null) {
            throw new ServiceException("编辑导航，id不能为空");
        }
        Navigation find = navigationRepository.selectById(updateCmd.getId());
        if (find == null) {
            throw new ServiceException(StrUtil.format("编辑的导航未找到，id：{}", updateCmd.getId()));
        }
//        if (!find.getCategory().equals(updateCmd.getCategory())) {
//            throw new ServiceException("编辑导航时，不允许修改导航分类");
//        }
        updateCmd.validate();
        Navigation navigation = assembler.toNavigation(updateCmd);
        if (navigation.getParentId() == null) {
            navigation.setParentId(SysConst.NAVIGATION_ROOT_ID);
        }
        validateParent(navigation, navigation.getParentId());
        if (navigation.getFunctionId() != null) {
            functionRepository.exist(navigation.getFunctionId());
        }
        navigation.autoCompleteUpdateData();
        navigationRepository.updateNavigation(navigation);
    }

    public void validateParent(Navigation navigation, Long parentId) {
        if (navigation.getCategory().equals(NavigationCategory.MODULE.getType()) && parentId != 0L) {
            throw new ServiceException("模块只能在最外层，父id必须为空或者0");
        }
        if (!navigation.getCategory().equals(NavigationCategory.MODULE.getType())) {
            if (parentId == 0L) {
                throw new ServiceException("只有模块可以在最外层");
            } else {
                Navigation parent = navigationRepository.selectById(parentId);
                if (parent == null) {
                    throw new ServiceException(StrUtil.format("父节点未找到，parentId:{}", parentId));
                }
                if (navigation.getCategory().equals(NavigationCategory.CATEGORY.getType()) && !parent.getCategory().equals(NavigationCategory.MODULE.getType())) {
                    throw new ServiceException("分类只能在模块里");
                }
                if (navigation.getCategory().equals(NavigationCategory.GROUP.getType())
                        && (parent.getCategory().equals(NavigationCategory.GROUP.getType()) || parent.getCategory().equals(NavigationCategory.NAVIGATION.getType()))) {
                    throw new ServiceException("分组只能在模块或分类里");
                }
                if (navigation.getCategory().equals(NavigationCategory.NAVIGATION.getType()) && parent.getCategory().equals(NavigationCategory.NAVIGATION.getType())) {
                    throw new ServiceException("导航只能在模块、分类、分组里");
                }
            }
        }
    }

    @Transactional
    @Override
    public void removeNavigation(Long id) {
        Navigation navigation = navigationRepository.selectById(id);
        if (navigation == null) {
            throw new ServiceException(StrUtil.format("导航未找到，id:{}", id));
        }
        Long count = navigationRepository.selectCount(navigation.getId());
        if (count > 0) {
            throw new ServiceException("包含子级导航，不允许删除");
        }
        // 删除成员权限：如果成员的功能权限只对应一个导航，且删除的导航就是这个导航，则需要删除成员权限
        if (navigation.getCategory().equals(NavigationCategory.NAVIGATION.getType())
                && navigation.getNavigationType().equals(NavigationType.FUNCTION.getType())
                && navigation.getFunctionId() != null) {
            // 需要删除的成员权限
            List<MemberPermissionDO> deleteMemberPermissions = new ArrayList<>();
            // 该功能有这些导航
            List<Navigation> navigationsOfThisFunction = navigationRepository.selectByFunctionId(navigation.getFunctionId());
            List<Long> navigationIds = navigationsOfThisFunction.stream().map(Navigation::getId).toList();
            // 该功能有这些权限组
            List<FunctionPermissionDO> functionPermissions = functionPermissionRepository.list(navigation.getFunctionId());
            if (CollUtil.isNotEmpty(functionPermissions)) {
                List<Long> funPermIds = functionPermissions.stream().map(FunctionPermissionDO::getId).toList();
                // 该功能有这些成员具有相关权限
                List<MemberPermissionDO> memberPermissions = memberPermissionRepository.selectByFunctionPermIds(funPermIds);
                if (CollUtil.isNotEmpty(memberPermissions)) {
                    List<Long> memberIds = memberPermissions.stream().map(MemberPermissionDO::getMemberId).toList();
                    // 这些成员在该功能的所有导航权限
                    List<MemberNavigationDO> memberNavigations = memberNavigationRepository.selectByMemberIdsAndNavigationIds(memberIds, navigationIds);
                    // 按成员分组区分是否删除成员的权限
                    Map<Long, List<MemberNavigationDO>> groupByMember = memberNavigations.stream().collect(Collectors.groupingBy(MemberNavigationDO::getMemberId));
                    groupByMember.forEach((memberId, list) -> {
                        // 如果成员的功能权限对应多个导航，删除其中一个导航，无需删除成员权限；
                        if (list.size() > 1) {
                            log.info("成员{}还有该功能的其他导航，保留功能权限", memberId);
                        }
                        // 如果成员的功能权限对应一个导航，且删除的导航就是这个导航，则需要删除成员权限；
                        if (list.size() == 1 && Objects.equals(list.get(0).getNavigationId(), id)) {
                            deleteMemberPermissions.addAll(memberPermissions.stream().filter(mp -> Objects.equals(mp.getMemberId(), memberId)).toList());
                        }
                    });
                    memberPermissionRepository.deleteMemberPermissions(deleteMemberPermissions);
                }
            }
        }
        // 删除成员导航权限
        memberNavigationRepository.deleteByNavigationId(List.of(id));
        // 删除导航
        navigationRepository.removeNavigation(id);
    }

    @Override
    public List<NavigationResult> listAll() {
        List<Navigation> navigations = navigationRepository.listAll();
        return assembler.toNavigationRs(navigations);
    }

    @Override
    public List<NavigationResult> listAllActive() {
        List<Navigation> navigations = navigationRepository.listAllActive();
        return assembler.toNavigationRs(navigations);
    }

    @Override
    public NavigationResult detail(Long id) {
        Navigation navigation = navigationRepository.selectById(id);
        if (navigation == null) {
            throw new ServiceException(StrUtil.format("导航未找到，id:{}", id));
        }
        return assembler.toNavigationR(navigation);
    }

    @Transactional
    @Override
    public void moveNavigation(NavigationMoveCmd moveCmd) {
        log.info("Navigation move start");
        if (moveCmd.getParentId() == null) {
            moveCmd.setParentId(SysConst.NAVIGATION_ROOT_ID);
        }
        Navigation find = navigationRepository.selectById(moveCmd.getId());
        if (find == null) {
            throw new ServiceException(StrUtil.format("移动的导航未找到，导航id：{}", moveCmd.getId()));
        }
        validateParent(find, moveCmd.getParentId());

        List<Navigation> children = navigationRepository.listChildren(moveCmd.getParentId());
        List<Navigation> sortNavigations = children.stream().filter(n -> !n.getId().equals(moveCmd.getId())).sorted(Comparator.comparing(Navigation::getOrderNum)).toList();
        for (int i = 0; i < sortNavigations.size(); i++) {
            Navigation navigation = sortNavigations.get(i);
            int order = moveCmd.getOrderNum() > i ? i : i + 1;
            if (order != navigation.getOrderNum()) {
                // 在这之后的菜单排序需要 +1
                navigationRepository.updateSort(navigation.getId(), order);
                log.debug("navigation {} {} update with order num {}.", navigation.getId(), navigation.getName(), order);
            } else {
                log.debug("navigation {} {} no need update", navigation.getId(), navigation.getName());
            }
        }
        navigationRepository.updateParentAndSort(moveCmd.getId(), moveCmd.getParentId(), moveCmd.getOrderNum());
        log.info("Navigation move end");
    }

    @Override
    public List<NavigationTree> buildNavigationTree(List<NavigationResult> results) {
        List<NavigationTree> navigationTreeList = assembler.toNavigationTree(results);
        return TreeUtils.toTree(navigationTreeList, 0L);
    }

    @Override
    public List<NavigationSelectResult> selectByFunctionId(Long functionId) {
        List<Navigation> navigations = navigationRepository.selectByFunctionId(functionId);
        return assembler.toNavigationSelectRs(navigations);
    }
}
