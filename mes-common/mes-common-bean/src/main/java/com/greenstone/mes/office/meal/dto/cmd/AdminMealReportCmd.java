package com.greenstone.mes.office.meal.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminMealReportCmd {

    @NotNull(message = "请选择用餐类型")
    private Integer mealType;

    private Integer reportType;

    private Boolean haveMeal;

    @NotNull(message = "请填写用餐数量")
    @Min(value = 1, message = "用餐数量不能小于1")
    private Integer mealNum;

    @NotNull(message = "请选择日期")
    private LocalDate day;

    private String remark;

    @NotNull(message = "请选择报餐人")
    private Long userId;

}
