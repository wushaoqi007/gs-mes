package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.system.api.domain.SysDictData;
import com.greenstone.mes.system.api.domain.SysDictType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteDictService {

    @GetMapping("/dict/data/list")
    List<SysDictData> getDictData(@RequestParam("dictType") String dictType);

    @PostMapping("/dict/data")
    void addDictData(@RequestBody SysDictData dict);


    @GetMapping("/dict/type/info/{dictType}")
    SysDictType selectDictTypeByType(@PathVariable("dictType") String dictType);
}
