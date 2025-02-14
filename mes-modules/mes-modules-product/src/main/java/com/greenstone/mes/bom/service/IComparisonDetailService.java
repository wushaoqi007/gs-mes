package com.greenstone.mes.bom.service;

import com.greenstone.mes.bom.domain.ComparisonDetail;
import com.greenstone.mes.common.mybatisplus.IServiceWrapper;

/**
 * ComparisonDetail接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface IComparisonDetailService extends IServiceWrapper<ComparisonDetail> {

    /**
     * 查询ComparisonDetail
     *
     * @param id ComparisonDetail主键
     * @return ComparisonDetail
     */
    ComparisonDetail selectComparisonDetailById(Long id);
}
