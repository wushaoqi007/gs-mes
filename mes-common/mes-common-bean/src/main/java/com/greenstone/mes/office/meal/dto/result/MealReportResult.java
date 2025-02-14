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
public class MealReportResult {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Long updateById;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
