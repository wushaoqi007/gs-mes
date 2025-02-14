package com.greenstone.mes.material.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class MachinedPartExportReq {

    @NotNull(message = "请选择开始时间")
    private Date startTime;

    @NotNull(message = "请选择结束时间")
    private Date endTime;

}
