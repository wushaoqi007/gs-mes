package com.greenstone.mes.oa.interfaces.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AttendanceMonthReq {

    @JsonFormat(pattern = "yyyy-MM")
    private Date month;

}
