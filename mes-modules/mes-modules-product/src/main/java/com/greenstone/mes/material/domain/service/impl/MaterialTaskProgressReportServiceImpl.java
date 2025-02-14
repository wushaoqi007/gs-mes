
package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskProgressReport;
import com.greenstone.mes.material.domain.service.IMaterialTaskProgressReportService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskProgressReportMapper;
import org.springframework.stereotype.Service;

/**
 * 任务的进度报告service业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskProgressReportServiceImpl extends ServiceImpl<MaterialTaskProgressReportMapper, MaterialTaskProgressReport> implements IMaterialTaskProgressReportService {
}
