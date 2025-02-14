package com.greenstone.mes.oa.infrastructure.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PunchMachine {
    // 自动化企业微信
    ZDH_KEJI("科技考勤"),
    ZDH_ZIDONGHUA1("自动化考勤（1）"),
    ZDH_ZIDONGHUA2("自动化考勤（2）"),
    ZDH_F3("3厂考勤"),
    ZDH_NANJINGOFFICE("南京办事处"),
    ZDH_LG_F5F4("5厂4楼【LG】"),
    ZDH_LG_CNA("CNA 【LG】"),
    ZDH_LG_F6P("6厂前工程【LG】"),
    ZDH_LG_F6P_2("6厂前工程【LG 】"),
    ZDH_LG_F5P("5厂前工程【LG】"),
    ZDH_LG_F4P("4厂前工程【LG】"),
    ZDH_LG_F6F1("6厂1楼后工程【LG】"),
    ZDH_LG_F4F4("4厂4楼【LG】"),

    // 科技企业微信
    KJ("Uface-BLT1"),

    // 无锡总部打卡机
    WX_NEW("无锡新厂房"),

    NULL(null),
    ;

    /**
     * 南京的打卡机
     */
    public static final List<PunchMachine> NAN_JING_PUNCH_MACHINE = new ArrayList<>();

    static {
        NAN_JING_PUNCH_MACHINE.add(ZDH_NANJINGOFFICE);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F5F4);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_CNA);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F6P);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F6P_2);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F5P);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F4P);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F6F1);
        NAN_JING_PUNCH_MACHINE.add(ZDH_LG_F4F4);
    }

    /**
     * 出差的打卡机
     */
    public static final List<PunchMachine> NOT_IN_WUXI = new ArrayList<>();

    static {
        NOT_IN_WUXI.add(ZDH_NANJINGOFFICE);
        NOT_IN_WUXI.add(ZDH_LG_F5F4);
        NOT_IN_WUXI.add(ZDH_LG_CNA);
        NOT_IN_WUXI.add(ZDH_LG_F6P);
        NOT_IN_WUXI.add(ZDH_LG_F6P_2);
        NOT_IN_WUXI.add(ZDH_LG_F5P);
        NOT_IN_WUXI.add(ZDH_LG_F4P);
        NOT_IN_WUXI.add(ZDH_LG_F6F1);
        NOT_IN_WUXI.add(ZDH_LG_F4F4);
    }

    /**
     * 无锡的打卡机
     */
    public static final List<PunchMachine> IN_WUXI = new ArrayList<>();

    static {
        IN_WUXI.add(WX_NEW);
        IN_WUXI.add(ZDH_F3);
        IN_WUXI.add(ZDH_KEJI);
    }

    public static final Map<String, PunchMachine> map = new HashMap<>();

    public static PunchMachine getByName(String name) {
        return map.computeIfAbsent(name, n -> {
            for (PunchMachine punchMachine : PunchMachine.values()) {
                if (n.equals(punchMachine.getName())) {
                    return punchMachine;
                }
            }
            return PunchMachine.NULL;
        });
    }

    private final String name;

    PunchMachine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isInNanJing() {
        return NAN_JING_PUNCH_MACHINE.contains(this);
    }

    public boolean isNotInWuXi() {
        return NOT_IN_WUXI.contains(this);
    }

    public boolean isInWuXi() {
        return IN_WUXI.contains(this);
    }
}
