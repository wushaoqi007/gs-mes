package com.greenstone.mes.office.meal.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealManageResult {
    private String id;
    private Integer mealType;
    private LocalDate day;
    private Integer reportNum;
    private Integer mealNum;
    private Integer mealUsedNum;
    private Integer mealRevokeNum;
    private Boolean stopped;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;

}
