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
public class MaterialComplaintStatisticsResp {

    /**
     * 问题环节（1:设计、2:品检、3:装配）
     */
    private String problemType;

    /**
     * 投诉率
     */
    private Double complaintRate;
}
