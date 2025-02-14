package com.greenstone.mes.material.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceiveRecordListQuery {

    private Date startTime;
    private Date endTime;

    private Long materialId;

    private Long userId;
}
