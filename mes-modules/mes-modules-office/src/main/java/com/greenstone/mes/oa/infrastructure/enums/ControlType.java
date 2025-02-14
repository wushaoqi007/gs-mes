package com.greenstone.mes.oa.infrastructure.enums;

public enum ControlType {
    Text("Text"),
    Textarea("Textarea"),
    Number("Number"),
    Money("Money"),
    Date("Date"),
    Selector("Selector"),
    Contact("Contact"),
    File("File"),
    Table("Table"),
    Attendance("Attendance"),
    Vacation("Vacation"),
    PunchCorrection("PunchCorrection"),
    DateRange("DateRange"),
    ;

    private final String name;

    ControlType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
