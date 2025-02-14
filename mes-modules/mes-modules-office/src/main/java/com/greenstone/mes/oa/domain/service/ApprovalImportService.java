package com.greenstone.mes.oa.domain.service;

import com.greenstone.mes.oa.application.dto.ApproVacationImportDTO;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/9 10:40
 */

public interface ApprovalImportService {

    void importVacation(List<ApproVacationImportDTO> approVacationImportDTOS);

}
