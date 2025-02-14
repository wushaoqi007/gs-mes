package com.greenstone.mes.common.core.enums;

/**
 * 入库出库单错误代码 12XXXX
 *
 */

public enum WarehouseIOError implements ServiceError {
    //  出入仓库
    E120101(120101, "选择的订单不存在"),
    E120102(120102, "不允许修改非草稿状态的单据"),
    E120103(120103, "只能够关闭完成状态的单据"),
    E120104(120104, "无法变更单据为草稿或提交以外的状态"),
    E120105(120105, "库存不足"),

    ;
    public final int code;

    public final String msg;

    WarehouseIOError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
