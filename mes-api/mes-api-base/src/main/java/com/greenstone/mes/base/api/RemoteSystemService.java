package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteSystemFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统远程调用接口
 *
 * @author wushaoqi
 * @date 2022-06-08-8:15
 */
@FeignClient(contextId = "remoteSystemService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSystemFallbackFactory.class)
public interface RemoteSystemService {

    /**
     * 获取部门信息
     */
    @GetMapping("/dept/list")
    R<List<SysDept>> getDeptList(@RequestParam("deptName") String name);

    /**
     * 获取部门信息
     */
    @GetMapping("/dept/info/byFullName")
    R<SysDept> getDeptInfo(@RequestParam("deptName") String name);


    /**
     * 获取用户信息
     */
    @GetMapping("/user/listAll")
    R<List<SysUser>> listAllUser();

    /**
     * 新增部门
     */
    @PostMapping("/dept")
    R<String> insertDept(@RequestBody SysDept dept);

    /**
     * 修改部门
     */
    @PutMapping("/dept")
    R<String> updateDept(@RequestBody SysDept dept);


    /**
     * 新增用户
     */
    @PostMapping("/user")
    R<String> insertUser(@RequestBody SysUser user);

    /**
     * 修改用户基本信息
     */
    @PutMapping("/user/basics")
    void updateUser(@RequestBody SysUser user);

    /**
     * 获取用户基本信息
     */
    @GetMapping("/user/basicInfo/{username}")
    R<SysUser> basicInfo(@PathVariable("username") String username);

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping("/config/configKey/{configKey}")
    R<String> getConfigValueByKey(@PathVariable("configKey") String configKey);

    @PostMapping("/sn/next")
    SerialNoR getNextSn(@RequestBody SerialNoNextCmd nextCmd);

    @PostMapping("/sn/short/next")
    SerialNoR nextShortSn(@RequestBody SerialNoNextCmd nextCmd);

    @PostMapping("/sn/next/contract")
    SerialNoR getNextCn(@RequestBody SerialNoNextCmd nextCmd);

    @PostMapping("/message")
    void sendSysMsg(MessageSaveCmd messageSaveCmd);

    @GetMapping("/user/info/public/{userId}")
    SysUser getUserPublicInfo(@PathVariable("userId") Long userId);

    @PostMapping("/message")
    void sendMessage(@RequestBody MessageSaveCmd messageSaveCmd);

    @GetMapping("/user/info/detail/{wxUserId}")
    SysUser getUserInfoByWxUserId(@PathVariable("wxUserId") String wxUserId);
}
