package com.greenstone.mes.oa.infrastructure.constant;

public interface AttendanceConst {

    interface CheckinType {
        int PUNCH_IN = 1;
        int PUNCH_OUT = 2;
    }

    interface ApprovalType {
        // 请假
        int LEAVE = 1;
        // 外出
        int GO_OUT = 2;
        // 出差
        int BUSINESS_TRIP = 4;
        // 加班
        int OVERTIME = 8;
        // 夜班
        int NIGHT_SHIFT = 16;
    }

    interface CalcType {
        // 迟到
        int COME_LATE = 1;
        // 早退
        int LEAVE_EARLY = 2;
        // 旷工
        int ABSENTEEISM = 4;
        // 请假
        int LEAVE = 8;
        // 加班
        int OVER_TIME = 16;
        // 工作时长
        int WORK_TIME = 32;
        // 出差
        int BUSINESS_TRIP = 64;
        // 所有
        int ALL = Integer.MAX_VALUE;
    }

    interface ControlType {
        // 文本
        String Text = "Text";
        // 多行文本
        String Textarea = "Textarea";
        // 数字
        String Number = "Number";
        // 金额
        String Money = "Money";
        // 日期/日期+时间
        String Date = "Date";
        // 单选/多选
        String Selector = "Selector";
        // 成员/部门
        String Contact = "Contact";
        // 说明文字
        String Tips = "Tips";
        // 附件
        String File = "File";
        // 明细
        String Table = "Table";
        // 假勤-出差/外出/加班组件
        String Attendance = "Attendance";
        // 请假
        String Vacation = "Vacation";
        // 补卡
        String PunchCorrection = "PunchCorrection";
        // 时长
        String DateRange = "DateRange";
    }

}
