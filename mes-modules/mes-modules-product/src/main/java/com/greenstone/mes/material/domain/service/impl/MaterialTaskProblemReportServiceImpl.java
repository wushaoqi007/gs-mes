
package com.greenstone.mes.material.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.domain.MaterialTaskProblemReport;
import com.greenstone.mes.material.domain.service.IMaterialTaskProblemReportService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialTaskProblemReportMapper;
import org.springframework.stereotype.Service;

/**
 * 任务的问题报告servic业务层处理
 *
 * @author wushaoqi
 * @date 2022-08-08-10:58
 */
@Service
public class MaterialTaskProblemReportServiceImpl extends ServiceImpl<MaterialTaskProblemReportMapper, MaterialTaskProblemReport> implements IMaterialTaskProblemReportService {
}
