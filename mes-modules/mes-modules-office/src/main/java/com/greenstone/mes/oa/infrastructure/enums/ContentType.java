package com.greenstone.mes.oa.infrastructure.enums;

import com.greenstone.mes.oa.domain.entity.*;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/19 16:46
 */

public record ContentType<T>(Control control, Class<T> dateType) {

    public static final ContentType<Date> DATE = new ContentType<>(Control.Date, Date.class);
    public static final ContentType<String> Text = new ContentType<>(Control.Text, String.class);
    public static final ContentType<ApprovalMembers> Contact = new ContentType<>(Control.Contact, ApprovalMembers.class);
    public static final ContentType<String> TEXTAREA = new ContentType<>(Control.Textarea, String.class);
    public static final ContentType<ApprovalContentFile> FILE = new ContentType<>(Control.File, ApprovalContentFile.class);
    public static final ContentType<ApprovalContentVacation> VACATION = new ContentType<>(Control.Vacation, ApprovalContentVacation.class);
    public static final ContentType<ApprovalContentAttendance> ATTENDANCE = new ContentType<>(Control.Attendance, ApprovalContentAttendance.class);
    public static final ContentType<ApprovalContentPunchCorrection> PUNCH_CORRECTION = new ContentType<>(Control.PunchCorrection, ApprovalContentPunchCorrection.class);

    public Control getControl() {
        return control;
    }

    public Class<T> getDateType() {
        return dateType;
    }

    public enum Control {
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
        Comments("comments"),
        ;

        private final String name;

        Control(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
