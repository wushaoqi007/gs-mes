package com.greenstone.mes.table.adapter;

import com.greenstone.mes.system.api.RemoteFunctionService;
import com.greenstone.mes.system.dto.result.FunctionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FunctionServiceAdapter {

    private final RemoteFunctionService functionService;

    public FunctionResult getFunctionById(Long functionId) {
        return CacheUtil.get("functionById", functionId, functionService::getFunction);
    }

}