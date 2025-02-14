package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class MachineOrderExportQuery {

    private String serialNo;

    private String provider;

    @NotEmpty(message = "导出月份不为空")
    @Length(min = 7, max = 7, message = "导出月份格式为：yyyy-MM")
    private String month;

    private LocalDate start;
    private LocalDate end;

}
