package com.greenstone.mes.common.security.interceptor;

import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.context.SecurityContextHolder;
import com.greenstone.mes.common.core.text.UUID;
import com.greenstone.mes.common.core.utils.ServletUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.security.auth.AuthUtil;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.api.model.LoginUser;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author ruoyi
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 设置 traceId
        String traceId = request.getHeader(SecurityConstants.TRACE_ID);
        if (traceId == null) {
            traceId = UUID.fastUUID().toString();
        }
        MDC.put(SecurityConstants.TRACE_ID, traceId);


        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));
        // 通过Feign调用的接口会设置一个特殊的HEADER，使得服务之间调用不鉴权，注意：依旧会校验token有效性
        SecurityContextHolder.set(SecurityConstants.FROM_SOURCE, ServletUtils.getHeader(request, SecurityConstants.FROM_SOURCE));

        String token = SecurityUtils.getToken();
        if (!"undefined".equals(token) && StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                MDC.put(SecurityConstants.USER_ID, String.valueOf(loginUser.getUserid()));
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();

        MDC.remove(SecurityConstants.TRACE_ID);
        MDC.remove(SecurityConstants.USER_ID);
    }
}
