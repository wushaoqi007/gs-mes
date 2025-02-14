package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckCountR {

    @Excel(name = "姓名")
    private String checkBy;
    @Excel(name = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
    @Excel(name = "零件数量")
    private Long partTotal;
    @Excel(name = "图纸数量")
    private Integer paperTotal;
}
