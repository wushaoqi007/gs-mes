package com.greenstone.mes.oa.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.oa.application.service.OaRankLevelUserRelationService;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelation;
import com.greenstone.mes.oa.infrastructure.mapper.OaRankLevelUserRelationMapper;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelUserRelationListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-01-8:35
 */
@Service
public class OaRankLevelUserRelationServiceImpl extends ServiceImpl<OaRankLevelUserRelationMapper, OaRankLevelUserRelation> implements OaRankLevelUserRelationService {

    @Autowired
    private OaRankLevelUserRelationMapper oaRankLevelUserRelationMapper;

    @Override
    public List<OaRankLevelUserRelationListResp> selectRankLevelUserRelationList(OaRankLevelUserRelationListReq oaRankLevelUserRelationListReq) {
        return oaRankLevelUserRelationMapper.selectRankLevelUserRelationList(oaRankLevelUserRelationListReq);
    }

    @Override
    public OaRankLevelUserRelationListResp selectRankLevelUserRelationById(Long id) {
        return oaRankLevelUserRelationMapper.selectRankLevelUserRelationById(id);
    }

}
