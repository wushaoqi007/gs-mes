package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaRankLevel;
import com.greenstone.mes.oa.request.OaRankLevelAddReq;
import com.greenstone.mes.oa.request.OaRankLevelEditReq;
import com.greenstone.mes.oa.response.OaRankLevelResp;

import java.util.List;

/**
 * 职级等级Service
 *
 * @author wushaoqi
 * @date 2022-05-31-14:28
 */
public interface OaRankLevelService extends IServiceWrapper<OaRankLevel> {

    void addRankLevel(OaRankLevelAddReq oaRankLevelAddReq);

    void updateRankLevel(OaRankLevelEditReq oaRankLevelEditReq);

    List<OaRankLevelResp> selectRankLevelList(OaRankLevel oaRankLevel);

    /**
     * 构建前端所需要下拉树结构
     */
    List<OaRankLevelResp> buildRankLevelTreeSelect(List<OaRankLevelResp> list);

    OaRankLevel getRecordDetail(Long recordId);

    boolean deleteRankLevelById(Long id);
}
