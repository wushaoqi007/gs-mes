package com.greenstone.mes.material.enums;

/**
 * 采购单零件进度
 */
public enum PurchasePartStatusCode {

    TO_CONFIRM("待确认", "1"),
    TO_RECEIVED("待收件", "2"),
    RECEIVING("收件中", "3"),
    RECEIVED("已收件", "4"),
    REWORK("返工中", "5"),
    SURFACE_TREATMENT("表处中", "6"),
    ABANDON("废弃", "7");


    private String label;
    private String value;

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    PurchasePartStatusCode(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public static String getLabelByValue(String value) {
        for (PurchasePartStatusCode purchasePartStatusCode : values()) {
            if (purchasePartStatusCode.getValue().equals(value)) {
                return purchasePartStatusCode.getLabel();
            }
        }
        return "";
    }

}
