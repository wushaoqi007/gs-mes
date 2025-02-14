package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.system.api.domain.SysDept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@Repository
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteDeptService {

    @PostMapping("/dept/innerDept")
    SysDept getSysDept(@RequestBody SysDept sysDept);

    @PostMapping("/dept")
    void addDept(@RequestBody SysDept sysDept);

    @PutMapping("/dept")
    void updateDept(@RequestBody SysDept sysDept);

    @DeleteMapping("/dept/{deptId}")
    void deleteDept(@PathVariable("deptId") Long deptId);
}
