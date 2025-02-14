package com.greenstone.mes.oa.domain.service.impl;

import com.greenstone.mes.oa.application.assembler.ApprovalAssembler;
import com.greenstone.mes.oa.application.dto.ApproVacationImportDTO;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import com.greenstone.mes.oa.domain.service.ApprovalImportService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/9 10:40
 */
@RequiredArgsConstructor
@Service
public class ApprovalImportServiceImpl implements ApprovalImportService {

    private final ApprovalAssembler approvalAssembler;
    private final WxApprovalRepository wxApprovalRepository;
    private final WxOaService externalWxOaService;


    @Override
    public void importVacation(List<ApproVacationImportDTO> approVacationImportDTOS) {

    }

}
