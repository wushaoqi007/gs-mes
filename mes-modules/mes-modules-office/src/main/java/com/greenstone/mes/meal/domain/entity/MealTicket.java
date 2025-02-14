package com.greenstone.mes.meal.domain.entity;

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
public class MealTicket {
    private String id;
    private String reportId;
    private Integer mealType;
    private String ticketCode;
    private LocalDate day;
    private Long reportById;
    private String reportBy;
    private Boolean used;
    private LocalDateTime useTime;
    private String wxMediaId;
}
