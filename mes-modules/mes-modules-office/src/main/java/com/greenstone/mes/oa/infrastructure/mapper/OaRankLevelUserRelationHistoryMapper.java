package com.greenstone.mes.oa.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelationHistory;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelExportDataListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OaRankLevelUserRelationHistoryMapper extends BaseMapper<OaRankLevelUserRelationHistory> {

    List<OaRankLevelExportDataListResp> listExportData(OaRankLevelUserRelationListReq rankLevel);
}
