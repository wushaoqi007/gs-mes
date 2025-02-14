package com.greenstone.mes.oa.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class JobCardPrintCmd {

    @NotEmpty(message = "请选择员工")
    @Size(max = 80, message = "单次最多打印200张")
    private List<Long> userIds;

}
