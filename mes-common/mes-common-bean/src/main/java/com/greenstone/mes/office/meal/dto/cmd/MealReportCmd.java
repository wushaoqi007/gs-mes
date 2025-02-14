package com.greenstone.mes.office.meal.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealReportCmd {

    private Integer mealType;

    private Integer reportType;

    private Boolean haveMeal;

    private Integer mealNum;

    private LocalDate day;

    private String remark;

    private String wxCpId;

    private String wxUserId;

}
