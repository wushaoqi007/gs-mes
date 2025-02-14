package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.material.response.PartBoardExportResp;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ExcelBuildService {

    /**
     * 导出零件看板
     */
    XSSFWorkbook exportPartsBoard(List<PartBoardExportResp> respList);

}
