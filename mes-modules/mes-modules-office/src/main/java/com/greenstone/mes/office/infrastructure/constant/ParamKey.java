package com.greenstone.mes.office.infrastructure.constant;

public interface ParamKey {

    interface MealReport {
        // 午餐报餐不需要打卡人员
        String EXCLUDE_LUNCH_CHECK_USER = "lunch_report_checkin_exclude_user";
        // 午餐报餐不需要打卡部门
        String EXCLUDE_LUNCH_CHECK_DEPT = "lunch_report_checkin_exclude_dept";
        // 午餐报餐不需要打卡标签
        String EXCLUDE_LUNCH_CHECK_TAG = "lunch_report_checkin_exclude_tag";
        // 晚餐报餐不需要加班申请人员
        String EXCLUDE_DINNER_CHECK_USER = "dinner_report_application_exclude_user";
        // 晚餐报餐不需要加班申请部门
        String EXCLUDE_DINNER_CHECK_DEPT = "dinner_report_application_exclude_dept";
        // 晚餐报餐不需要加班申请标签
        String EXCLUDE_DINNER_CHECK_TAG = "dinner_report_application_exclude_tag";
        // 新厂和三厂的蓝牙考情及名称
        String WUXI_CHECKIN_BLUETOOTH_NAME = "wuxi_checkin_bluetooth_name";
        // 报餐统计数据的接收人
        String STAT_DATA_RECEIVER = "stat_data_receiver";
    }

}
