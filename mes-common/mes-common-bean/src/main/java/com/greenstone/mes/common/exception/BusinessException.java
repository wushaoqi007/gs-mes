package com.greenstone.mes.common.exception;

import com.greenstone.mes.common.core.exception.ServiceException;

public class BusinessException extends ServiceException {

    private Integer code;

    private final String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
