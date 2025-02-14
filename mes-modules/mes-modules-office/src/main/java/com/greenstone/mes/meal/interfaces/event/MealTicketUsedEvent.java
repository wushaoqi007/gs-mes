package com.greenstone.mes.meal.interfaces.event;

import com.greenstone.mes.meal.domain.entity.MealTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealTicketUsedEvent {

    private MealTicket ticket;

}
