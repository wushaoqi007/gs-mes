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
public class MaterialReworkStatisticsResp {

    /**
     * 加工商
     */
    private String provider;

    /**
     * 总数
     */
    private Integer totalNum;

    /**
     * 返工率
     */
    private Double reworkRate;
}
