package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.material.domain.MaterialQualityInspectionRecord;
import com.greenstone.mes.material.request.MaterialComplainStatisticsReq;
import com.greenstone.mes.material.request.MaterialQualityStatisticsReq;
import com.greenstone.mes.material.response.MaterialComplaintStatisticsResp;
import com.greenstone.mes.material.response.MaterialQualityHourStatisticsResp;
import com.greenstone.mes.material.response.MaterialReworkStatisticsResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-09-14-14:45
 */
@Repository
public interface MaterialQualityInspectionRecordMapper extends BaseMapper<MaterialQualityInspectionRecord> {

    /**
     * 返工率统计查询
     * @param statisticsReq
     * @return
     */
    List<MaterialReworkStatisticsResp> selectReworkStatistics(MaterialComplainStatisticsReq statisticsReq);
    /**
     * 投诉率统计查询
     * @param statisticsReq
     * @return
     */
    List<MaterialComplaintStatisticsResp> selectComplaintStatistics(MaterialComplainStatisticsReq statisticsReq);

    /**
     * 查询当天质检检验数量统计
     */
    List<MaterialQualityHourStatisticsResp> selectHourStatistics(MaterialQualityStatisticsReq qualityStatisticsReq);
}
