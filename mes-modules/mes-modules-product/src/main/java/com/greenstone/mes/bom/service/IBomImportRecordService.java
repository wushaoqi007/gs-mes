package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.request.BomImportRecordReq;
import com.greenstone.mes.bom.response.BomImportRecordListResp;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

import java.util.List;

/**
 * BOMImportRecordService接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface IBomImportRecordService extends IServiceWrapper<BomImportRecord> {

    /**
     * 查询BOM导入记录
     *
     * @param id 主键
     * @return BomImportRecord
     */
    BomImportRecord selectBomImportRecordById(Long id);

    /**
     * 查询BOM导入记录列表
     *
     * @param bomImportRecordReq
     */
    List<BomImportRecordListResp> selectBomImportRecordList(BomImportRecordReq bomImportRecordReq);
}
