package com.greenstone.mes.material.enums;

/**
 * 购买原因
 */
public enum MaterialPurchaseReasonCode {

    ZCXZ(1, "正常新增"),
    SJSW(2, "设计失误"),
    XQBG(3, "需求变更"),
    CKDS(4, "仓库丢失"),
    ZPDS(5, "装配丢失"),
    QT(6, "其他");


    private Integer label;
    private String value;

    public Integer getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    MaterialPurchaseReasonCode(Integer label, String value) {
        this.label = label;
        this.value = value;
    }

    public static Integer getLabelByValue(String value) {
        for (MaterialPurchaseReasonCode code : values()) {
            if (code.getValue().equals(value)) {
                return code.getLabel();
            }
        }
        return 0;
    }

}
