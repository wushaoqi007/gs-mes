package com.greenstone.mes.material.infrastructure.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NgType {
    A("A", "外观"),
    B("B", "加工"),
    C("C", "材质处理"),
    A1("A1", "外观"),
    A2("A2", "毛刺"),
    A3("A3", "划痕"),
    A4("A4", "粗糙"),
    B1("B1", "尺寸"),
    B2("B2", "孔位"),
    B3("B3", "孔径"),
    B4("B4", "孔深"),
    B5("B5", "偏尺寸"),
    B6("B6", "平行度"),
    B7("B7", "垂直度"),
    B8("B8", "倒角"),
    B9("B9", "槽宽"),
    B10("B10", "台阶"),
    B11("B11", "螺纹"),
    B12("B12", "漏加工"),
    B13("B13", "加工错误"),
    C1("C1", "淬火"),
    C2("C2", "材质");

    private final String type;

    private final String name;

    NgType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static NgType getByType(String type) {
        return Arrays.stream(NgType.values()).filter(s -> s.getType().equals(type)).findFirst().orElse(null);
    }

    public static List<NgType> getParentNgType() {
        return Arrays.stream(NgType.values()).filter(s -> s.getType().length() == 1).collect(Collectors.toList());
    }

    public static List<NgType> getSubNgType(String parentType) {
        return Arrays.stream(NgType.values()).filter(s -> s.getType().contains(parentType) && !s.getType().equals(parentType)).collect(Collectors.toList());
    }
}
