package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.DrawingComparison;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

/**
 * Comparison接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface IDrawingComparisonService extends IServiceWrapper<DrawingComparison> {

    /**
     * 查询Comparison
     *
     * @param id Comparison主键
     * @return Comparison
     */
    DrawingComparison selectComparisonById(Long id);
}
