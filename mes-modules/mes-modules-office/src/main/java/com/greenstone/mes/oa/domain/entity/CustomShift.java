package com.greenstone.mes.oa.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author wsqwork
 * @date 2024/12/12 15:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomShift {

    private Long id;
    @NotEmpty(message = "名称不能为空")
    private String name;
    @NotNull(message = "早班时段不能为空")
    private String dayShift;
    @NotNull(message = "晚班时段不能为空")
    private String nightShift;
    @NotEmpty(message = "地点不能为空")
    private String location;
    @NotNull(message = "纬度不能为空")
    private Double lat;
    @NotNull(message = "经度不能为空")
    private Double lng;
    @NotNull(message = "距离不能为空")
    private Double distance;
    @NotEmpty(message = "早班休息时间不能为空")
    private String dayRestTime;
    @NotEmpty(message = "晚班休息时间不能为空")
    private String nightRestTime;

}
