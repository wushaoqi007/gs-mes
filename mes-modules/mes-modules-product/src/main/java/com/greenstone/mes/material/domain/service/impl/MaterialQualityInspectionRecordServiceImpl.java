package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialQualityInspectionRecord;
import com.greenstone.mes.material.enums.ProblemType;
import com.greenstone.mes.material.infrastructure.mapper.MaterialQualityInspectionRecordMapper;
import com.greenstone.mes.material.request.MaterialComplainStatisticsReq;
import com.greenstone.mes.material.request.MaterialQualityInspectionListReq;
import com.greenstone.mes.material.request.MaterialQualityStatisticsReq;
import com.greenstone.mes.material.response.MaterialComplaintStatisticsResp;
import com.greenstone.mes.material.response.MaterialQualityHourStatisticsResp;
import com.greenstone.mes.material.response.MaterialReworkStatisticsResp;
import com.greenstone.mes.material.domain.service.IMaterialQualityInspectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author wushaoqi
 * @date 2022-09-14-14:45
 */
@Service
public class MaterialQualityInspectionRecordServiceImpl extends ServiceImpl<MaterialQualityInspectionRecordMapper, MaterialQualityInspectionRecord> implements IMaterialQualityInspectionRecordService {

    @Autowired
    private MaterialQualityInspectionRecordMapper qualityInspectionRecordMapper;

    @Override
    public List<MaterialQualityInspectionRecord> getQualityInspectionList(MaterialQualityInspectionListReq qualityInspectionRecord) {
        QueryWrapper<MaterialQualityInspectionRecord> queryWrapper = Wrappers.query(MaterialQualityInspectionRecord.builder().build());
        if (StrUtil.isNotBlank(qualityInspectionRecord.getProjectCode())) {
            queryWrapper.like("project_code", qualityInspectionRecord.getProjectCode());
        }
        if (StrUtil.isNotBlank(qualityInspectionRecord.getCode())) {
            queryWrapper.like("code", qualityInspectionRecord.getCode());
        }
        if (StrUtil.isNotBlank(qualityInspectionRecord.getCreateBy())) {
            queryWrapper.like("create_by", qualityInspectionRecord.getCreateBy());
        }
        if (StrUtil.isNotBlank(qualityInspectionRecord.getNgType())) {
            queryWrapper.like("ng_type", qualityInspectionRecord.getNgType());
        }
        if (StrUtil.isNotBlank(qualityInspectionRecord.getNgSubclass())) {
            queryWrapper.like("ng_subclass", qualityInspectionRecord.getNgSubclass());
        }
        queryWrapper.orderByDesc("create_time");

        return list(queryWrapper);
    }

    @Override
    public List<MaterialReworkStatisticsResp> selectReworkStatistics(MaterialComplainStatisticsReq statisticsReq) {
        return qualityInspectionRecordMapper.selectReworkStatistics(statisticsReq);
    }

    @Override
    public List<MaterialComplaintStatisticsResp> selectComplaintStatistics(MaterialComplainStatisticsReq statisticsReq) {
        // 查询投诉率统计
        List<MaterialComplaintStatisticsResp> statisticsList = qualityInspectionRecordMapper.selectComplaintStatistics(statisticsReq);
        // 数据为空时，仍需展示所有问题环节，投诉率为0
        for (ProblemType value : ProblemType.values()) {
            Optional<MaterialComplaintStatisticsResp> first = statisticsList.stream().filter(s -> s.getProblemType().equals(value.getType())).findFirst();
            if (first.isEmpty()) {
                statisticsList.add(MaterialComplaintStatisticsResp.builder().complaintRate(0D).problemType(value.getType()).build());
            }
        }
        statisticsList.sort(Comparator.comparing(MaterialComplaintStatisticsResp::getProblemType));
        return statisticsList;
    }

    @Override
    public List<MaterialQualityHourStatisticsResp> selectHourStatistics(MaterialQualityStatisticsReq qualityStatisticsReq) {
        return qualityInspectionRecordMapper.selectHourStatistics(qualityStatisticsReq);
    }
}
