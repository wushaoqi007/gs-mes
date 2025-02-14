package com.greenstone.mes.meal.application.assembler;

/**
 * @author gu_renkai
 * @date 2022/11/24 9:23
 */

import com.greenstone.mes.meal.application.dto.excel.MealReportExcel;
import com.greenstone.mes.meal.domain.entity.MealReport;
import com.greenstone.mes.meal.infrastructure.persistence.MealManageDo;
import com.greenstone.mes.office.meal.dto.cmd.MealReportCmd;
import com.greenstone.mes.office.meal.dto.result.MealManageResult;
import com.greenstone.mes.office.meal.dto.result.MealReportResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MealAssembler {

    MealReport addCmd2Entity(MealReportCmd cmd);

    MealManageResult do2result(MealManageDo mealManageDo);

    List<MealManageResult> manageDos2results(List<MealManageDo> mealManageDos);

    MealReportResult entity2result(MealReport mealReport);

    List<MealReportResult> reportEntities2results(List<MealReport> mealReports);

    MealReportExcel result2excel(MealReportResult mealReportResult);

    List<MealReportExcel> results2excels(List<MealReportResult> mealReportResults);
}
