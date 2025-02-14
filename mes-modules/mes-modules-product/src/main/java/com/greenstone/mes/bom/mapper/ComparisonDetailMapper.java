package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.ComparisonDetail;
import org.springframework.stereotype.Repository;

/**
 * @author wushaoqi
 * @date 2022-05-16-13:55
 */
@Repository
public interface ComparisonDetailMapper extends BaseMapper<ComparisonDetail> {
    /**
     * 查询ComparisonDetail
     *
     * @param id ComparisonDetail主键
     * @return ComparisonDetail
     */
    ComparisonDetail selectComparisonDetailById(Long id);
}
