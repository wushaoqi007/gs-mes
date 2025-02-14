package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelation;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelUserRelationListResp;

import java.util.List;

/**
 * 职级关系Service
 *
 * @author wushaoqi
 * @date 2022-06-01-8:33
 */
public interface OaRankLevelUserRelationService extends IServiceWrapper<OaRankLevelUserRelation> {

    List<OaRankLevelUserRelationListResp> selectRankLevelUserRelationList(OaRankLevelUserRelationListReq oaRankLevelUserRelationListReq);

    OaRankLevelUserRelationListResp selectRankLevelUserRelationById(Long id);

}
