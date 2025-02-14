package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelationHistory;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelExportDataListResp;

import java.util.List;

/**
 * 职级关系Service
 *
 * @author wushaoqi
 * @date 2022-06-01-8:33
 */
public interface OaRankLevelUserRelationHistoryService extends IServiceWrapper<OaRankLevelUserRelationHistory> {

    List<OaRankLevelExportDataListResp> listExportData(OaRankLevelUserRelationListReq rankLevel);
}
