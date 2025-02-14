package com.greenstone.mes.oa.interfaces.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AttendanceMonthStatReq {

    @JsonFormat(pattern = "yyyy-MM")
    private Date monthStart;

}
