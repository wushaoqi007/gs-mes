package com.greenstone.mes.oa.application.manager;

import com.greenstone.mes.oa.request.OaRankLevelUserRelationAddReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationEditReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author wushaoqi
 * @date 2022-06-01-8:29
 */
public interface OaRankLevelManager {

    /**
     * 检查职级是否存在关系
     */
    boolean checkRankLevelExistRelation(Long id);

    void addRankLevelUserRelation(OaRankLevelUserRelationAddReq oaRankLevelUserRelationAddReq);

    boolean deleteRankLevelUserRelationById(Long id);

    boolean deleteRankLevelById(Long id);

    void updateRankLevelUserRelation(OaRankLevelUserRelationEditReq oaRankLevelUserRelationEditReq);

    XSSFWorkbook exportRankLevel(OaRankLevelUserRelationListReq rankLevel);
}
