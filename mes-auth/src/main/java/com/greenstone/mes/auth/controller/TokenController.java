package com.greenstone.mes.auth.controller;

import com.greenstone.mes.auth.form.LoginBody;
import com.greenstone.mes.auth.form.RegisterBody;
import com.greenstone.mes.auth.service.SysLoginService;
import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.utils.JwtUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.security.auth.AuthUtil;
import com.greenstone.mes.common.security.service.TokenService;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * token 控制
 *
 * @author ruoyi
 */
@RestController
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private RemoteOaService oaService;

    @PostMapping("/login")
    public R<?> login(@RequestBody LoginBody form) {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("/refresh")
    public R<?> refresh(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterBody registerBody) {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }

    @GetMapping("/qywx/qrCode/{cpId}")
    public AjaxResult loginQrCode(@PathVariable("cpId") String cpId) {
        return AjaxResult.success(oaService.qrCode(cpId));
    }

    @PostMapping("/login/qywx")
    public R<?> workwxLogin(@RequestBody WorkwxOauth2Cmd oauth2Cmd) {
        LoginUser loginUser = sysLoginService.workwxOAuth2Login(oauth2Cmd);
        return R.ok(tokenService.createToken(loginUser));
    }

}
