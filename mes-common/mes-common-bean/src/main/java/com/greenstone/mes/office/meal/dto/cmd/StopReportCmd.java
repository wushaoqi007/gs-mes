package com.greenstone.mes.office.meal.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StopReportCmd {

    @NotNull(message = "请指定用餐类型")
    private Integer mealType;

    @NotNull(message = "请指定日期")

    private LocalDate day;

}
