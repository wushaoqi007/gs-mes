
package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author wushaoqi
 * @date 2022-10-24-10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialComplainStatisticsReq {

    /**
     * 统计的年月日期
     */
    @NotEmpty(message = "material.quality.date.is.not.empty")
    @Pattern(regexp = "^([1-9]\\d{3}-)(([0]{0,1}[1-9])|([1][0-2]))$",message = "date format:yyyy-MM")
    private String date;

}
