package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuAddCmd;
import com.greenstone.mes.system.dto.cmd.CustomFormMenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "remoteMenuService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteMenuService {

    @DeleteMapping("/menu/{menuId}")
    void deleteMenu(@PathVariable("menuId") String menuId);

    @GetMapping("/menu/{menuId}")
    MenuBriefResult getBriefForm(@PathVariable("menuId") String menuId);

    /**
     * 获取表单定义
     */
    @GetMapping("/menu/{menuId}/form/definition")
    FormDefinitionVo getFormDefinition(@PathVariable("menuId") String menuId);

    /**
     * 新增自定义表单的菜单
     */
    @PostMapping("/menu/form/custom")
    void addCustomFormMenu(@RequestBody CustomFormMenuAddCmd menuAddCmd);

    /**
     * 更新自定义表单菜单
     */
    @PutMapping("/menu/form/custom")
    FormDefinitionVo editCustomFormMenu(@RequestBody CustomFormMenuEditCmd editCmd);
}
