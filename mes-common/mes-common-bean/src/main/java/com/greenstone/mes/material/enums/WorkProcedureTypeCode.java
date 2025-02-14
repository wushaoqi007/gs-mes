package com.greenstone.mes.material.enums;

/**
 * 工序类型字典
 */
public enum WorkProcedureTypeCode {

    DSJ("待收件", "0"),
    DZJ("待质检", "1"),
    ZJZ("质检中", "2"),
    DBC("待表处", "3"),
    BCZ("表处中", "4"),
    DFG("待返工", "5"),
    FGZ("返工中", "6"),
    HGP("合格品", "7"),
    LP("良品", "8"),
    WZ("未知", "9");


    private String label;
    private String value;

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    WorkProcedureTypeCode(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public static String getLabelByValue(String value) {
        for (WorkProcedureTypeCode typeCode : values()) {
            if (typeCode.getValue().equals(value)) {
                return typeCode.getLabel();
            }
        }
        return "";
    }

}
