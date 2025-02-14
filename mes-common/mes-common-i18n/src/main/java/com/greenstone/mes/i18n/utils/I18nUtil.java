package com.greenstone.mes.i18n.utils;

import com.greenstone.mes.common.core.utils.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class I18nUtil {

    public static String message(String code) {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public static String message(String code, String defaultMessage) {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
    }

}
