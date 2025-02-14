package com.greenstone.mes.oa.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.oa.application.service.OaRankLevelUserRelationHistoryService;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelationHistory;
import com.greenstone.mes.oa.infrastructure.mapper.OaRankLevelUserRelationHistoryMapper;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelExportDataListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-01-8:35
 */
@Service
public class OaRankLevelUserRelationHistoryServiceImpl extends ServiceImpl<OaRankLevelUserRelationHistoryMapper, OaRankLevelUserRelationHistory> implements OaRankLevelUserRelationHistoryService {

    @Autowired
    private OaRankLevelUserRelationHistoryMapper rankLevelUserRelationHistoryMapper;

    @Override
    public List<OaRankLevelExportDataListResp> listExportData(OaRankLevelUserRelationListReq rankLevel) {
        return rankLevelUserRelationHistoryMapper.listExportData(rankLevel);
    }
}
