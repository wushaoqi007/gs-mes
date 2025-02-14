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
public class MealRevokeCmd {

    private String reportId;

    private Integer mealType;

    private Integer revokeType;

    private LocalDate day;

    private String wxCpId;

    private String wxUserId;
}
