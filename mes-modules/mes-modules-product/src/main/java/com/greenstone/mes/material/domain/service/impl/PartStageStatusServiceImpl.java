package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.PartStageStatus;
import com.greenstone.mes.material.infrastructure.mapper.PartStageStatusMapper;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.request.PartsReworkReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.request.PartsUsedReq;
import com.greenstone.mes.material.response.PartReworkResp;
import com.greenstone.mes.material.response.PartReworkStatResp;
import com.greenstone.mes.material.response.PartStageStatusListResp;
import com.greenstone.mes.material.response.PartUsedResp;
import com.greenstone.mes.material.domain.service.PartStageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 零件阶段状态ServiceImpl
 *
 * @author wushaoqi
 * @date 2022-12-13-13:34
 */
@Service
public class PartStageStatusServiceImpl extends ServiceImpl<PartStageStatusMapper, PartStageStatus> implements PartStageStatusService {

    private final PartStageStatusMapper partStageStatusMapper;

    @Autowired
    public PartStageStatusServiceImpl(PartStageStatusMapper partStageStatusMapper) {
        this.partStageStatusMapper = partStageStatusMapper;
    }

    @Override
    public List<PartUsedResp> selectPartUsedList(PartsUsedReq partsUsedReq) {
        return partStageStatusMapper.selectPartUsedList(partsUsedReq);
    }

    @Override
    public List<PartReworkResp> selectPartReworkList(PartsReworkReq partsReworkReq) {
        return partStageStatusMapper.selectPartReworkList(partsReworkReq);
    }

    @Override
    public List<PartReworkStatResp> partProviderStat(PartsReworkStatReq partsReworkStatReq) {
        return partStageStatusMapper.partProviderStat(partsReworkStatReq);
    }

    @Override
    public List<PartReworkStatResp> partReworkStat(PartsReworkStatReq partsReworkStatReq) {
        return partStageStatusMapper.partReworkStat(partsReworkStatReq);
    }

    @Override
    public List<PartStageStatusListResp> selectProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        return partStageStatusMapper.selectProgressList(progressStatReq);
    }
}
