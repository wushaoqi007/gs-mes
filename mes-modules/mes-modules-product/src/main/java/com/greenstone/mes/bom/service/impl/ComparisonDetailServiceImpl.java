package com.greenstone.mes.bom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.bom.domain.ComparisonDetail;
import com.greenstone.mes.bom.mapper.ComparisonDetailMapper;
import com.greenstone.mes.bom.service.IComparisonDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ComparisonDetailService业务层处理
 *
 * @author wushaoqi
 * @date 2022-05-11-13:00
 */
@Service
public class ComparisonDetailServiceImpl extends ServiceImpl<ComparisonDetailMapper, ComparisonDetail> implements IComparisonDetailService {


    @Autowired
    private ComparisonDetailMapper comparisonDetailMapper;


    @Override
    public ComparisonDetail selectComparisonDetailById(Long id) {
        return comparisonDetailMapper.selectComparisonDetailById(id);
    }
}
