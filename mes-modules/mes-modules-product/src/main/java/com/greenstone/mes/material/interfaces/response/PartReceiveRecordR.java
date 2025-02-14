package com.greenstone.mes.material.interfaces.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceiveRecordR {

    private Long id;

    private String sponsor;

    private Long sponsorId;

    private String receiveTime;

    private Integer handleNum;

    private Integer total;

}
