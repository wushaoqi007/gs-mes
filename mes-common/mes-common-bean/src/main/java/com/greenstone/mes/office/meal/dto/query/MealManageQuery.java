package com.greenstone.mes.office.meal.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealManageQuery {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDay;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDay;

    private Integer mealType;

    private Integer reportType;

    private String reportBy;

}
