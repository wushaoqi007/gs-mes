package com.greenstone.mes.meal.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("meal_ticket")
public class MealTicketDo {
    @TableId(type = IdType.ASSIGN_UUID)
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
