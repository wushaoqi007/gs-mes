package com.greenstone.mes.system.application.service;

import com.greenstone.mes.system.application.dto.cmd.FunctionAddCmd;
import com.greenstone.mes.system.application.dto.cmd.FunctionMoveCmd;
import com.greenstone.mes.system.application.dto.result.FunctionPermissionResult;
import com.greenstone.mes.system.domain.entity.Function;
import com.greenstone.mes.system.dto.result.FunctionResult;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-18-8:37
 */
public interface FunctionService {
    void saveFunction(FunctionAddCmd addCmd);

    void updateFunction(FunctionAddCmd updateCmd);

    void removeFunction(Long id);

    List<FunctionResult> listAll();

    List<Function> getFunctions();

    FunctionResult detail(Long id);

    void moveFunction(FunctionMoveCmd sortCmd);

    List<FunctionPermissionResult> listAllFunctionWithPerm();
}
