package com.greenstone.mes.material.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingStatusListResp {


    /**
     * 状态
     */
    private Integer status;

    /**
     * 对应状态变更的时间
     */
    private LocalDateTime changeTime;

    /**
     * 变更人
     */
    private String changeBy;

}
