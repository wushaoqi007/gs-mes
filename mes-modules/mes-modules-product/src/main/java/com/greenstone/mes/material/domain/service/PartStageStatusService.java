package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.PartStageStatus;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.request.PartsReworkReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.request.PartsUsedReq;
import com.greenstone.mes.material.response.PartReworkResp;
import com.greenstone.mes.material.response.PartReworkStatResp;
import com.greenstone.mes.material.response.PartStageStatusListResp;
import com.greenstone.mes.material.response.PartUsedResp;

import java.util.List;

/**
 * 零件阶段状态Service
 *
 * @author wushaoqi
 * @date 2022-12-13-13:32
 */
public interface PartStageStatusService extends IServiceWrapper<PartStageStatus> {
    /**
     * 已领用零件查询
     *
     * @return 已领用零件
     */
    List<PartUsedResp> selectPartUsedList(PartsUsedReq partsUsedReq);

    /**
     * 返工零件查询
     *
     * @return 返工零件
     */
    List<PartReworkResp> selectPartReworkList(PartsReworkReq partsReworkReq);

    /**
     * 查询所有加工商加工信息
     */
    List<PartReworkStatResp> partProviderStat(PartsReworkStatReq partsReworkStatReq);

    /**
     * 查询所有返工加工商返工信息
     */
    List<PartReworkStatResp> partReworkStat(PartsReworkStatReq partsReworkStatReq);

    /**
     * 查询进度完整信息
     *
     * @param progressStatReq
     */
    List<PartStageStatusListResp> selectProgressList(MaterialWorksheetProgressStatReq progressStatReq);

}
