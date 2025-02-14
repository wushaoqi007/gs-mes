package com.greenstone.mes.material.enums;

/**
 * 工序类型转换仓库dept_code
 */
public enum WorkProcedureTypeToWareHouseCode {

    DZJ("DJ", "1"),
    ZJZ("ZJ", "2"),
    DBC("BC", "3"),
    BCZ("BCZ", "4"),
    DFG("FG", "5"),
    FGZ("FGZ", "6"),
    HGP("HG", "7"),
    LP("LP", "8");


    private String label;
    private String value;

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    WorkProcedureTypeToWareHouseCode(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public static String getLabelByValue(String value) {
        for (WorkProcedureTypeToWareHouseCode typeCode : values()) {
            if (typeCode.getValue().equals(value)) {
                return typeCode.getLabel();
            }
        }
        return "";
    }

}
