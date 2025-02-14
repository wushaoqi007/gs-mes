package com.greenstone.mes.common.core.exception;

import com.greenstone.mes.common.core.enums.ServiceError;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author ruoyi
 */
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer errCode;

    /**
     * 提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(ServiceError error) {
        this.errCode = error.getCode();
        this.message = error.getMsg();
    }

    public ServiceException(ServiceError error, String message) {
        this.errCode = error.getCode();
        this.message = error.getMsg() + "," + message;
    }

    public ServiceException(int error, String message) {
        this.errCode = error;
        this.message = message;
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public Integer getErrCode() {
        return errCode;
    }

    @Override
    public String getMessage() {
        return message;
    }


}