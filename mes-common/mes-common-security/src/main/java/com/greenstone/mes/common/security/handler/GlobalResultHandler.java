package com.greenstone.mes.common.security.handler;

import com.greenstone.mes.common.core.constant.SecurityConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.security.annotation.RawResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/19 16:48
 */
@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        if (returnType.getMethodAnnotation(RawResponse.class) != null) {
            return false;
        }
        if (parameterType == AjaxResult.class || parameterType == TableDataInfo.class || parameterType == R.class || parameterType == ResponseEntity.class) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (isInnerRequest(request)) {
            return body;
        }

        return AjaxResult.success(body);
    }

    private boolean isInnerRequest(ServerHttpRequest request) {
        List<String> list = request.getHeaders().get(SecurityConstants.FROM_SOURCE);
        if (list != null && list.size() > 0) {
            return SecurityConstants.INNER.equals(list.get(0));
        }
        return false;
    }

}
