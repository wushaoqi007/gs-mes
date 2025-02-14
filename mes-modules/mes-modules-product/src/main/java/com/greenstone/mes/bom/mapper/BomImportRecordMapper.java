package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.request.BomImportRecordReq;
import com.greenstone.mes.bom.response.BomImportRecordListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BomImportRecordMapper接口
 *
 * @author wushaoqi
 * @date 2022-05-11-13:02
 */
@Repository
public interface BomImportRecordMapper extends BaseMapper<BomImportRecord> {
    /**
     * 查询BOM导入记录
     *
     * @param id BOM主键
     * @return BOM
     */
    BomImportRecord selectBomImportRecordById(Long id);

    /**
     * 查询BOM导入记录列表
     *
     * @param bomImportRecordReq
     */
    List<BomImportRecordListResp> selectBomImportRecordList(BomImportRecordReq bomImportRecordReq);
}
