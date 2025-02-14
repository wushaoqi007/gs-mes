package com.greenstone.mes.system.application.service;

import com.greenstone.mes.system.application.dto.cmd.NavigationAddCmd;
import com.greenstone.mes.system.application.dto.cmd.NavigationMoveCmd;
import com.greenstone.mes.system.application.dto.result.NavigationResult;
import com.greenstone.mes.system.application.dto.result.NavigationSelectResult;
import com.greenstone.mes.system.application.dto.result.NavigationTree;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-18-8:37
 */
public interface NavigationService {
    void saveNavigation(NavigationAddCmd addCmd);

    void updateNavigation(NavigationAddCmd updateCmd);

    void removeNavigation(Long id);

    List<NavigationResult> listAll();

    List<NavigationResult> listAllActive();

    NavigationResult detail(Long id);

    void moveNavigation(NavigationMoveCmd sortCmd);

    List<NavigationTree> buildNavigationTree(List<NavigationResult> results);

    List<NavigationSelectResult> selectByFunctionId(Long functionId);
}
