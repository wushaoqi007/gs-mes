package com.greenstone.mes.office.meal.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketUseStatQuery {

    @NotNull(message = "请指定开始时间")
    private LocalDateTime useTimeStart;

    @NotNull(message = "请指定结束时间")
    private LocalDateTime useTimeEnd;

}
