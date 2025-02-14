package com.greenstone.mes.material.enums;

/**
 * 物料管理 错误码 02XXXX
 */
public enum ErrorCode {
    /**
     * 库存错误码 0201XX
     */
    NO_ENOUGH_STOCK(20101);

    private final Integer code;

    ErrorCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
