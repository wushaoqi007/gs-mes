package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.api.factory.RemoteUserFallbackFactory;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.auth.UserLoginAuth;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.dto.result.UserResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    @GetMapping("/users/byLoginUsername")
    UserLoginAuth getUserByLoginUsername(@RequestParam("loginUsername") String loginUsername);

    @PostMapping("/user")
    SysUser addUser(SysUser user);

    @PutMapping("/user")
    SysUser updateUser(SysUser user);

    @GetMapping("/user/withoutEmpNo")
    List<SysUser> getUsersWithoutEmpNo();

    @GetMapping("/users/byWx")
    User getByWx(@RequestParam("wxCpId") String wxCpId, @RequestParam("wxUserId") String wxUserId);

    @GetMapping("/users/{userId}")
    User getById(@PathVariable("userId") Long userId);

    @GetMapping("/users/getByMail/{mail}")
    User getByMail(@PathVariable("mail") String mail);


    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过用户Id查询用户名
     *
     * @param userId 用户Id
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/name/{userId}")
    R<String> getUserNameById(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/user/info/name/{userId}")
    String getNickName(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/user/basicInfo/{username}")
    SysUser basicInfo(@PathVariable("username") String username);

    @GetMapping("/user/userinfo/{userId}")
    SysUser userinfo(@PathVariable("userId") Long userId);

    @PutMapping("/user/changeStatus")
    void changeStatus(@RequestBody SysUser user);

    @PostMapping("/user/innerUser")
    SysUser getUser(@RequestBody SysUser user);

    @PostMapping("/user/innerUsers")
    List<SysUser> getUsers(@RequestBody SysUser user);

    @DeleteMapping("/user/{userIds}")
    void remove(@PathVariable("userIds") Long[] userIds);

    @GetMapping("/user/listAll")
    List<SysUser> listAll();

    @PostMapping("/user/brief/corp/greenstone")
    List<UserResult> gsUserBriefs(@RequestBody UserQuery query);

    @PostMapping("/user/workwx")
    List<UserResult> userWorkwxInfos(@RequestBody UserQuery query);

    @PutMapping("/user/basics")
    void basicsEdit(@RequestBody SysUser user);

    @GetMapping("/users/{userId}/permissions")
    List<UserPermissionResult> getUserPermissions(@PathVariable("userId") Long userId);

    @GetMapping("/users/{userId}/navigations")
    List<MemberNavigationResult> getUserNavigations(@PathVariable("userId") Long userId);

    @GetMapping("/users/{userId}/functions")
    List<MemberFunctionResult> getUserFunctions(@PathVariable("userId") Long userId);

}
