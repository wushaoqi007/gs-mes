package com.greenstone.mes.system.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.system.domain.converter.NavigationConverter;
import com.greenstone.mes.system.domain.entity.Navigation;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.infrastructure.mapper.NavigationMapper;
import com.greenstone.mes.system.infrastructure.po.NavigationDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-17-16:33
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class NavigationRepository {
    private final NavigationMapper navigationMapper;
    private final NavigationConverter converter;


    public void saveNavigation(Navigation navigation) {
        NavigationDO navigationDO = converter.entity2Do(navigation);
        navigationMapper.insert(navigationDO);
    }

    public void updateNavigation(Navigation navigation) {
        NavigationDO navigationDO = converter.entity2Do(navigation);
        navigationMapper.updateById(navigationDO);
    }

    public void removeNavigation(Long id) {
        navigationMapper.deleteById(id);
    }

    public List<MemberNavigationResult> listAllOfAdmin() {
        LambdaQueryWrapper<NavigationDO> queryWrapper = Wrappers.lambdaQuery(NavigationDO.class);
        queryWrapper.orderByAsc(NavigationDO::getOrderNum);
        List<NavigationDO> navigationDOS = navigationMapper.selectList(queryWrapper);
        return converter.dos2Results(navigationDOS);
    }

    public List<Navigation> listAll() {
        LambdaQueryWrapper<NavigationDO> queryWrapper = Wrappers.lambdaQuery(NavigationDO.class);
        queryWrapper.orderByAsc(NavigationDO::getOrderNum);
        List<NavigationDO> navigationDOS = navigationMapper.selectList(queryWrapper);
        return converter.dos2Entities(navigationDOS);
    }

    public List<Navigation> listAllActive() {
        LambdaQueryWrapper<NavigationDO> queryWrapper = Wrappers.lambdaQuery(NavigationDO.class);
        queryWrapper.eq(NavigationDO::getActive, true);
        queryWrapper.orderByAsc(NavigationDO::getOrderNum);
        List<NavigationDO> navigationDOS = navigationMapper.selectList(queryWrapper);
        return converter.dos2Entities(navigationDOS);
    }

    public List<Navigation> selectByFunctionId(Long functionId) {
        LambdaQueryWrapper<NavigationDO> queryWrapper = Wrappers.lambdaQuery(NavigationDO.class);
        queryWrapper.eq(NavigationDO::getFunctionId, functionId);
        queryWrapper.eq(NavigationDO::getActive, true);
        List<NavigationDO> navigationDOS = navigationMapper.selectList(queryWrapper);
        return converter.dos2Entities(navigationDOS);
    }

    public List<Navigation> listChildren(Long parentId) {
        LambdaQueryWrapper<NavigationDO> queryWrapper = Wrappers.lambdaQuery(NavigationDO.class).eq(NavigationDO::getParentId, parentId);
        queryWrapper.orderByAsc(NavigationDO::getOrderNum);
        List<NavigationDO> navigationDOS = navigationMapper.selectList(queryWrapper);
        return converter.dos2Entities(navigationDOS);
    }

    public Navigation selectById(Long id) {
        NavigationDO navigationDO = navigationMapper.selectById(id);
        return converter.do2Entity(navigationDO);
    }

    public Long selectCount(Long parentId) {
        return navigationMapper.selectCount(NavigationDO.builder().parentId(parentId).build());
    }

    public void updateSort(Long id, Integer orderNum) {
        LambdaUpdateWrapper<NavigationDO> updateWrapper = Wrappers.lambdaUpdate(NavigationDO.class)
                .eq(NavigationDO::getId, id).set(NavigationDO::getOrderNum, orderNum);
        navigationMapper.update(updateWrapper);
    }

    public void updateParentAndSort(Long id, Long parentId, Integer orderNum) {
        LambdaUpdateWrapper<NavigationDO> updateWrapper = Wrappers.lambdaUpdate(NavigationDO.class)
                .eq(NavigationDO::getId, id).set(NavigationDO::getOrderNum, orderNum).set(NavigationDO::getParentId, parentId);
        navigationMapper.update(updateWrapper);
    }

    public void removeNavigationByFunctionId(Long functionId) {
        LambdaQueryWrapper<NavigationDO> deleteWrapper = Wrappers.lambdaQuery(NavigationDO.class).eq(NavigationDO::getFunctionId, functionId);
        navigationMapper.delete(deleteWrapper);
    }

}
