package com.greenstone.mes.material.response;

import lombok.*;

/**
 * @author wushaoqi
 * @date 2022-10-26-9:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialQualityHourStatisticsResp {

    /**
     * 小时
     */
    private Integer hour;

    /**
     * 总量
     */
    private Double num;
}
