package com.greenstone.mes.oa.infrastructure.constant;

public class AttendanceParam {

    // 出差补贴-白班
    public static int ALLOWANCE_TRIP_DAY;
    // 出差补贴-晚班
    public static int ALLOWANCE_TRIP_NIGHT;


    // 允许迟到的时间（分）
    public static int ALLOWED_LATE_MINUTES;
    // 允许早退的时间（分）
    public static int ALLOWED_EARLY_MINUTES;
    // 允许迟到早退的次数
    public static int ALLOWED_REMIT_TIMES;

    // 加班的最小时间（分，少于此时间不算加班）
    public static int MIN_EXTRAWORK_MINUTES;


    // 早班的休息时间（以标准上班时间为标准增加偏移量，如 3:30-4:30）
    public static String REST_TIME_DAY;
    // 晚班的休息时间（以标准上班时间为标准增加偏移量，如 4:00-5:00）
    public static String REST_TIME_NIGHT;
    // 早班且出差的加班休息时间（以标准下班时间为标准增加偏移量，如 2:00-2:30）
    public static String REST_TIME_DAY_TRIP_EXTRAWORK;
    // 早班且不出差且生产部门的加班休息时间（以标准下班时间为标准增加偏移量，如 0:30-1:00）
    public static String REST_TIME_DAY_NOTRIP_PRODDEPT_EXTRAWORK;

    // 属于生产的部门
    public static String PROD_DEPT;

    // 生产中心部门
    public static String PROD_CENTER_DEPT;

    // 属于零件的部门
    public static String PARTS_DEPT;

    // 属于研发的部门
    public static String RD_DEPT;

    // 属于设计的部门
    public static String DESIGN_DEPT;

    // 白名单：加班申请的部门
    public static String WHITELIST_DEPT_EXTRA_WORK_APPROVAL;

    // 白名单部门下排除的人：加班申请的部门
    public static String WHITELIST_DEPT_EXCLUSION_USERID_EXTRA_WORK_APPROVAL;

    // 白名单：加班申请的人
    public static String WHITELIST_USERID_EXTRA_WORK_APPROVAL;

    // 无锡打卡机
    public static String PUNCHMACHINE_IN_WUXI;

    // 南京打卡机
    public static String PUNCHMACHINE_IN_NANJING;

    // 不计算加班的人员（仅标记）
    public static String NOT_COUNT_EXTRA_WORK;

    // 质检取件单签字审批模板id
    public static String MACHINE_CHECK_TAKE_SIGN_TAMP_ID;

    // 合格品取件单签字审批模板id
    public static String MACHINE_CHECKED_TAKE_SIGN_TAMP_ID;

    // 出库单签字审批模板id
    public static String MACHINE_FINISHED_SIGN_TAMP_ID;

    // 启用的企业微信cropId
    public static String ENABLE_QYWX_CROPID;

}
