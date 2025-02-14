package com.greenstone.mes.meal.domain.entity;

import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealReport {

    private String id;
    private Integer reportType;
    private Integer mealType;
    private Boolean haveMeal;
    private Integer mealNum;
    private Integer usedNum;
    private LocalDate day;
    private String remark;
    private Boolean revoked;
    private Integer revokeType;
    private Long reportById;
    private String reportBy;
    private String reportByNo;
    private Long deptId;
    private String deptName;
    private Long createById;
    private String createBy;
    private LocalDateTime createTime;
    private Long updateById;
    private String updateBy;
    private LocalDateTime updateTime;

    List<MealTicket> mealTickets;

    public void addMealTicket(MealTicket mealTicket) {
        if (this.mealTickets == null) {
            this.mealTickets = new ArrayList<>();
        }
        this.mealTickets.add(mealTicket);
    }

    public String getMealName(){
        return MealConst.MealType.LUNCH == this.mealType ? "午餐" : "晚餐";
    }
}
