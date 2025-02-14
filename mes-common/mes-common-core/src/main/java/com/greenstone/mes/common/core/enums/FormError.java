package com.greenstone.mes.common.core.enums;

/**
 * 单据错误代码 7XXXX
 *
 * @author gu_renkai
 * @date 2022/11/8 9:41
 */

public enum FormError implements ServiceError {
    E70101(70101, "单据不存在"),
    E70102(70102, "不允许修改非草稿状态的单据"),
    E70103(70103, "只能够操作完成状态的单据"),
    E70104(70104, "已审批的单据不能继续审批"),
    E70105(70105, "请选择物品档案中的物品"),

    ;
    public final int code;

    public final String msg;

    FormError(int code, String msg) {
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
