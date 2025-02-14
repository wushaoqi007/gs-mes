package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.BomImportRecord;
import com.greenstone.mes.bom.mapper.BomImportRecordMapper;
import com.greenstone.mes.bom.request.BomImportRecordReq;
import com.greenstone.mes.bom.response.BomImportRecordListResp;
import com.greenstone.mes.bom.service.IBomImportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BOMImportRecordService业务层处理
 *
 * @author wushaoqi
 * @date 2022-05-11-13:00
 */
@Service
public class BomImportRecordServiceImpl extends ServiceImpl<BomImportRecordMapper, BomImportRecord> implements IBomImportRecordService {

    @Autowired
    private BomImportRecordMapper bomImportRecordMapper;

    @Override
    public BomImportRecord selectBomImportRecordById(Long id) {
        return bomImportRecordMapper.selectBomImportRecordById(id);
    }

    @Override
    public List<BomImportRecordListResp> selectBomImportRecordList(BomImportRecordReq bomImportRecordReq) {
        return bomImportRecordMapper.selectBomImportRecordList(bomImportRecordReq);
    }
}
