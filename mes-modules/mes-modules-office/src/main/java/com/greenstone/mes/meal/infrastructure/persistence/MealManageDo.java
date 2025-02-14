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
@TableName("meal_manage")
public class MealManageDo {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private Integer mealType;
    private LocalDate day;
    private Integer reportNum;
    private Integer mealNum;
    private Integer mealUsedNum;
    private Integer mealRevokeNum;
    private Boolean stopped;
    private LocalDateTime stopTime;
    private Boolean additionalReportStopped;
    private LocalDateTime additionalReportStopTime;

}
