package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.dto.cmd.DormMemberOperationCmd;
import com.greenstone.mes.oa.dto.cmd.DormSaveCmd;
import com.greenstone.mes.oa.dto.cmd.DormUpdateCmd;
import com.greenstone.mes.oa.dto.query.DormListQuery;
import com.greenstone.mes.oa.dto.query.DormRecordQuery;
import com.greenstone.mes.oa.dto.result.*;
import com.greenstone.mes.oa.enums.DormCityType;

import java.util.List;

public interface DormService {

    DormResult detail(String dormNo);

    List<DormTreeResult> tree();

    List<DormResult> cities();

    List<DormResult> list(DormListQuery query);

    List<DormResult> detailList(DormListQuery query);

    DormMemberResult getDormMember(Long employeeId);

    DormSaveResult add(DormSaveCmd addCmd);

    DormSaveResult update(DormUpdateCmd updateCmd);

    void dormOperation(DormMemberOperationCmd operationCmd);

    void remove(String dormNo);

    List<DormExportResult> exportDorm(DormCityType cityType);

    List<DormRecordResult> records(DormRecordQuery cityType);

}
