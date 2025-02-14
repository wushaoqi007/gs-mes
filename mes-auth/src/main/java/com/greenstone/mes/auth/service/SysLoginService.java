package com.greenstone.mes.auth.service;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.constant.Constants;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ServletUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.ip.IpUtils;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.RemoteLogService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysLogininfor;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.auth.UserLoginAuth;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.wxcp.api.RemoteWxOAuthService;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.wxcp.resp.WxOauth2Resp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */

@RequiredArgsConstructor
@Slf4j
@Component
public class SysLoginService {
    private final RemoteLogService remoteLogService;

    private final RemoteUserService remoteUserService;

    private final RemoteWxOAuthService wxOAuthService;

    /**
     * 登录
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户不存在/密码错误");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户不存在/密码错误");
        }
        UserLoginAuth userLoginAuth = remoteUserService.getUserByLoginUsername(username);

        if (userLoginAuth == null) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("用户不存在/密码错误");
        }

        if (!SecurityUtils.matchesPassword(password, userLoginAuth.getPassword())) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码错误");
            throw new ServiceException("用户不存在/密码错误");
        }


        // 查询用户信息
        User user = remoteUserService.getById(userLoginAuth.getUserId());

//        if (R.FAIL == userResult.getCode()) {
//            throw new ServiceException(userResult.getMsg());
//        }

//        User userInfo = userResult.getData();

//        recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");

        List<UserPermissionResult> userPermissions = remoteUserService.getUserPermissions(userLoginAuth.getUserId());

        LoginUser loginUser = new LoginUser();
        loginUser.setPermissions(new HashSet<>(userPermissions));
        loginUser.setUser(user);
        loginUser.setUserid(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setLoginTime(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        return loginUser;
    }

    public void logout(String loginName) {
        recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        R<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode()) {
            throw new ServiceException(registerResult.getMsg());
        }
        recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
        logininfor.setMsg(message);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus("0");
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            logininfor.setStatus("1");
        }
        remoteLogService.saveLogininfor(logininfor, SecurityConstants.INNER);
    }

    public LoginUser workwxOAuth2Login(WorkwxOauth2Cmd oauth2Cmd) {
        log.info("微信oauth2登录， {}", oauth2Cmd);
        WxOauth2Resp wxOauth2Resp = wxOAuthService.getUserByOauth2(oauth2Cmd);
        if (wxOauth2Resp == null || StrUtil.isBlank(wxOauth2Resp.getUserId())) {
            throw new RuntimeException("当前企业不存在此用户，请联系管理员。");
        }
        User user = remoteUserService.getByWx(wxOauth2Resp.getCpId(), wxOauth2Resp.getUserId());
        if (user == null) {
            throw new RuntimeException("账号未注册，请前往企业微信应用‘格林司通管理系统’创建账号");
        }

        List<UserPermissionResult> userPermissions = remoteUserService.getUserPermissions(user.getUserId());
        LoginUser loginUser = new LoginUser();
        loginUser.setPermissions(new HashSet<>(userPermissions));
        loginUser.setUser(user);
        loginUser.setUserid(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setLoginTime(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));

        return loginUser;
    }
}