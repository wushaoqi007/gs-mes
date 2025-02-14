package com.greenstone.mes.office.meal.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketUseStatResult {

    private String useTime;

    private Integer number;

}
