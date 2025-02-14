package com.greenstone.mes.common.security.handler;

import com.greenstone.mes.common.core.constant.HttpStatus;
import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.exception.DemoModeException;
import com.greenstone.mes.common.core.exception.InnerAuthException;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.exception.auth.NotPermissionException;
import com.greenstone.mes.common.core.exception.auth.NotRoleException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.i18n.utils.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AjaxResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request,
                                                            HttpServletResponse response) {
        setStatus500IfInnerRequest(request, response);
        log.error("请求参数缺失或格式错误: {}", e.getMessage(), e);
        return AjaxResult.error(HttpStatus.BAD_REQUEST, "请求参数缺失或格式错误：" + e.getMessage());
    }

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public AjaxResult handleNotPermissionException(NotPermissionException e, HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public AjaxResult handleNotRoleException(NotRoleException e, HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                          HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(e, MDC.get(SecurityConstants.TRACE_ID));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常. ", requestURI, e);
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常", requestURI, e);
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(getI18nMessage(message, message));
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(getI18nMessage(message, message));
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public AjaxResult handleInnerAuthException(InnerAuthException e, HttpServletRequest request, HttpServletResponse response) {
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e, HttpServletRequest request, HttpServletResponse response) {
        setStatus500IfInnerRequest(request, response);
        return AjaxResult.error("演示模式，不允许操作");
    }

    private String getI18nMessage(String code, String defaultMessage) {
        if (StringUtils.isNotEmpty(code)) {
            return I18nUtil.message(code, defaultMessage);
        }
        return code;
    }

    private void setStatus500IfInnerRequest(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityConstants.INNER.equals(request.getHeader(SecurityConstants.FROM_SOURCE))) {
            response.setStatus(500);
        }
    }
}
