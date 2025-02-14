
package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskWorkReport;
import com.greenstone.mes.material.domain.service.IMaterialTaskWorkReportService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskWorkReportMapper;
import org.springframework.stereotype.Service;

/**
 * 任务的工作报告service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskWorkReportServiceImpl extends ServiceImpl<MaterialTaskWorkReportMapper, MaterialTaskWorkReport> implements IMaterialTaskWorkReportService {
}
