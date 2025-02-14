package com.greenstone.mes.oa.infrastructure.enums;

import com.greenstone.mes.oa.domain.entity.ApprovalContentFile;
import com.greenstone.mes.oa.domain.entity.ApprovalContentVacation;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/17 14:07
 */

public enum ContentEnum {
    DATE("Date", Date.class),
    TEXTAREA("Textarea", String.class),
    FILE("File", ApprovalContentFile.class),
    VACATION("Vacation", ApprovalContentVacation.class),
    ;

    private final String name;
    private final Class<?> dateType;

    ContentEnum(String name, Class<?> dateType) {
        this.name = name;
        this.dateType = dateType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getDateType() {
        return dateType;
    }
}
