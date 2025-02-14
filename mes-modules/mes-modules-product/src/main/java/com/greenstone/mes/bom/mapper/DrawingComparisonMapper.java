package com.greenstone.mes.bom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.bom.domain.DrawingComparison;
import org.springframework.stereotype.Repository;

/**
 * DrawingComparisonMapper接口
 *
 * @author wushaoqi
 * @date 2022-05-11-13:02
 */
@Repository
public interface DrawingComparisonMapper extends BaseMapper<DrawingComparison> {
    /**
     * 查询Comparison
     *
     * @param id Comparison主键
     * @return Comparison
     */
    DrawingComparison selectComparisonById(Long id);
}
