package com.greenstone.mes.meal.domain.converter;

import com.greenstone.mes.meal.domain.entity.MealReport;
import com.greenstone.mes.meal.domain.entity.MealTicket;
import com.greenstone.mes.meal.infrastructure.persistence.MealReportDo;
import com.greenstone.mes.meal.infrastructure.persistence.MealTicketDo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MealConverter {

    MealReportDo entity2Do(MealReport mealReport);

    List<MealReport> reportDos2Entities(List<MealReportDo> mealReportDos);

    MealTicketDo entity2Do(MealTicket mealTicket);

    List<MealTicketDo> entities2Dos(List<MealTicket> mealTickets);

    MealTicket do2Entity(MealTicketDo mealTicketDo);

    MealReport do2Entity(MealReportDo mealReportDo);

    List<MealTicket> ticketDos2Entities(List<MealTicketDo> mealTicketDos);

}
