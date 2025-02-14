package com.greenstone.mes.oa.enums;

/**
 * 企业微信审批状态
 */
public enum ApprovalStatusCode {

    SPZ(1, "审批中"),
    YTY(2, "已通过"),
    YBH(3, "已驳回"),
    YCX(4, "已撤销"),
    TGHCX(6, "通过后撤销"),
    YSC(7, "已删除"),
    YZF(10, "已支付");


    private Integer label;
    private String value;

    public Integer getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    ApprovalStatusCode(Integer label, String value) {
        this.label = label;
        this.value = value;
    }

    public static Integer getLabelByValue(String value) {
        for (ApprovalStatusCode code : values()) {
            if (code.getValue().equals(value)) {
                return code.getLabel();
            }
        }
        return 0;
    }

}
