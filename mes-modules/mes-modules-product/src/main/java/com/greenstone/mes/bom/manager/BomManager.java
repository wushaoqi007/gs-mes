package com.greenstone.mes.bom.manager;

import com.greenstone.mes.bom.domain.*;
import com.greenstone.mes.bom.dto.BomImportDTO;
import com.greenstone.mes.bom.dto.BomQrCodeAddDto;
import com.greenstone.mes.bom.request.*;
import com.greenstone.mes.bom.response.BomExportResp;
import com.greenstone.mes.bom.response.BomIntegrityResp;
import com.greenstone.mes.bom.response.BomPartExportResp;
import com.greenstone.mes.bom.response.BomQueryResp;

import java.util.List;

public interface BomManager {

    /**
     * BOM齐套检查
     *
     * @param integrityReq 需要检查的信息
     * @return 齐套检查结果
     */
    BomIntegrityResp integrity(BomIntegrityReq integrityReq);

    BomQueryResp getBom(Long bomId);

    List<BomExportResp> exportBomPart(BomExportReq exportReq);

    List<BomPartExportResp> exportCompareItems(BomCompareExportReq exportReq);

    void addBom(List<BomImportDTO> bomImportDTOList);

    /**
     * 插入bom导入记录
     */
    void importBomRecord(Bom bom, List<BomImportDTO> bomImportDTOList);

    /**
     * 新增bom导入记录及详情
     */
    void addBomImportRecord(BomImportRecord bomImportRecord, List<BomImportDetail> bomImportDetailList);

    /**
     * 新增图纸比对结果和详情
     */
    void addComparisonResultAndDetail(DrawingComparison drawingComparison, List<ComparisonDetail> comparisonDetails);

    void addBom(Bom bom, List<BomDetail> bomDetailList, boolean updateSupport);

    void publish(Bom bom);

    void delete(Long bomId, boolean force);

    void update(Bom bom, List<BomDetail> bomDetailList);

    /**
     * 从二维码添加BOM
     *
     * @param bomQrCodeAddDto bomQrCodeAddDto
     */
    void addFromQrCode(BomQrCodeAddDto bomQrCodeAddDto);

    /**
     * 修改机加工单导致的更新bom
     * @param bomEditByPartOrderReqList
     */
    void updateBomByPartOrder(List<BomEditByPartOrderReq> bomEditByPartOrderReqList);
}
