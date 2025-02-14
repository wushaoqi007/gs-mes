package com.greenstone.mes.oa.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelation;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelUserRelationListResp;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OaRankLevelUserRelationMapper extends BaseMapper<OaRankLevelUserRelation> {

    List<OaRankLevelUserRelationListResp> selectRankLevelUserRelationList(OaRankLevelUserRelationListReq oaRankLevelUserRelationListReq);

    OaRankLevelUserRelationListResp selectRankLevelUserRelationById(Long id);
}
