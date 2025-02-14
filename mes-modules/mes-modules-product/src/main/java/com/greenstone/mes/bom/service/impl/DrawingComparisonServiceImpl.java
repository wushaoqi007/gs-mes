package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.DrawingComparison;
import com.greenstone.mes.bom.mapper.DrawingComparisonMapper;
import com.greenstone.mes.bom.service.IDrawingComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ComparisonDetailService业务层处理
 *
 * @author wushaoqi
 * @date 2022-05-11-13:00
 */
@Service
public class DrawingComparisonServiceImpl extends ServiceImpl<DrawingComparisonMapper, DrawingComparison> implements IDrawingComparisonService {


    @Autowired
    private DrawingComparisonMapper drawingComparisonMapper;


    @Override
    public DrawingComparison selectComparisonById(Long id) {
        return drawingComparisonMapper.selectComparisonById(id);
    }
}
