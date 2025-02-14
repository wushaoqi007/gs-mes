package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-08-29-13:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceiveRecord {
    private Long id;

    private String sponsor;

    private Long sponsorId;

    private String receiveTime;

    private Integer handleNum;

    private Integer total;

}
