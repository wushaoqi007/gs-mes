package com.greenstone.mes.common.security.feign;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * @author gu_renkai
 * @date 2022/12/1 15:45
 */
@Slf4j
@Configuration
public class ExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        if (response != null) {
            try {
                Reader reader = response.body().asReader(Charset.defaultCharset());
                String result = IOUtils.toString(reader);
                AjaxResult ajaxResult = JSONObject.parseObject(result, AjaxResult.class);
                if (ajaxResult.get(AjaxResult.ERR_CODE_TAG) != null) {
                    throw new ServiceException((int) ajaxResult.get(AjaxResult.ERR_CODE_TAG), (String) ajaxResult.get(AjaxResult.MSG_TAG));
                } else {
                    if (ajaxResult.containsKey(AjaxResult.MSG_TAG)) {
                        throw new ServiceException((String) ajaxResult.get(AjaxResult.MSG_TAG));
                    } else {
                        throw new ServiceException(String.valueOf(ajaxResult));
                    }
                }
            } catch (IOException e) {
                log.error("Feign error, decoder error: {} ", e.getMessage(), e);
            }
        }
        return new ServiceException(SysError.E10001);
    }
}
