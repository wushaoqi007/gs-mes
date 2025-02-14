package com.greenstone.mes.oa.interfaces.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AttendanceRangeStatReq {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date start;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date end;

    private String cpId;

}
