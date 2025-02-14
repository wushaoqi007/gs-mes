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
@TableName("meal_report")
public class MealReportDo {

    @TableId(type = IdType.ASSIGN_UUID)
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

}
