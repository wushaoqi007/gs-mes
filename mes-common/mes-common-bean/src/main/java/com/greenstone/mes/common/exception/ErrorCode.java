package com.greenstone.mes.common.exception;

public interface ErrorCode {

    /**
     * 物料仓储模块错误 10000
     */
    // 物料不存在
    Integer UNKNOWN_MATERIAL = 10001;
    // 物料已存在
    Integer DUPLICATED_MATERIAL = 10002;
    // 库存不足
    Integer UNDER_STOCK = 10031;

}
