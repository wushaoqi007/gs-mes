package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.application.dto.result.IssueScanResult;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface MaterialIssueService {

    List<IssueScanResult> listByMaterialCode(String materialCode);

    void importMaterialIssue(Workbook wb, String fileName);

}
