package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.MaterialQualityInspectionRecord;
import com.greenstone.mes.material.request.MaterialComplainStatisticsReq;
import com.greenstone.mes.material.request.MaterialQualityInspectionListReq;
import com.greenstone.mes.material.request.MaterialQualityStatisticsReq;
import com.greenstone.mes.material.response.MaterialComplaintStatisticsResp;
import com.greenstone.mes.material.response.MaterialQualityHourStatisticsResp;
import com.greenstone.mes.material.response.MaterialReworkStatisticsResp;

import java.util.List;

/**
 * 质检记录Service
 *
 * @author wushaoqi
 * @date 2022-09-14-14:44
 */
public interface IMaterialQualityInspectionRecordService extends IServiceWrapper<MaterialQualityInspectionRecord> {

    /**
     * 查询质检记录列表
     *
     * @param qualityInspectionRecord
     * @return
     */
    List<MaterialQualityInspectionRecord> getQualityInspectionList(MaterialQualityInspectionListReq qualityInspectionRecord);

    /**
     * 查询返工率统计
     *
     * @param statisticsReq
     * @return
     */
    List<MaterialReworkStatisticsResp> selectReworkStatistics(MaterialComplainStatisticsReq statisticsReq);

    /**
     * 查询投诉率统计
     *
     * @param statisticsReq
     * @return
     */
    List<MaterialComplaintStatisticsResp> selectComplaintStatistics(MaterialComplainStatisticsReq statisticsReq);

    /**
     * 查询当天质检检验数量统计
     */
    List<MaterialQualityHourStatisticsResp> selectHourStatistics(MaterialQualityStatisticsReq qualityStatisticsReq);
}
