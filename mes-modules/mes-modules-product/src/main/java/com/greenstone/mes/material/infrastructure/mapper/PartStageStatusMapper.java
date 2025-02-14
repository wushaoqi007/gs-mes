package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.PartStageStatus;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.request.PartsReworkReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.request.PartsUsedReq;
import com.greenstone.mes.material.response.PartReworkResp;
import com.greenstone.mes.material.response.PartReworkStatResp;
import com.greenstone.mes.material.response.PartStageStatusListResp;
import com.greenstone.mes.material.response.PartUsedResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 零件阶段状态Mapper
 *
 * @author wushaoqi
 * @date 2022-12-13-13:31
 */
@Repository
public interface PartStageStatusMapper extends BaseMapper<PartStageStatus> {

    List<PartUsedResp> selectPartUsedList(PartsUsedReq partsUsedReq);

    List<PartReworkResp> selectPartReworkList(PartsReworkReq partsReworkReq);

    List<PartReworkStatResp> partProviderStat(PartsReworkStatReq partsReworkStatReq);

    List<PartReworkStatResp> partReworkStat(PartsReworkStatReq partsReworkStatReq);

    /**
     * 零件跟踪用（全阶段进度查询）
     */
    List<PartStageStatusListResp> selectProgressList(MaterialWorksheetProgressStatReq progressStatReq);
}
