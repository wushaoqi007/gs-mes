package com.greenstone.mes.oa.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/17 14:20
 */
@Data
@Builder
public class ApprovalContentAttendance {

    private DateRange dateRange;

    @Data
    @Builder
    public static class DateRange {
        private String type;

        private Date begin;

        private Date end;
    }

}
