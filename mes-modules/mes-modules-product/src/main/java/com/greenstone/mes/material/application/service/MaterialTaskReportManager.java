package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.domain.MaterialTaskProblemReport;
import com.greenstone.mes.material.domain.MaterialTaskProgressReport;
import com.greenstone.mes.material.domain.MaterialTaskWorkReport;
import com.greenstone.mes.material.request.MaterialTaskProblemReportAddReq;
import com.greenstone.mes.material.request.MaterialTaskProgressReportAddReq;
import com.greenstone.mes.material.request.MaterialTaskReportListReq;
import com.greenstone.mes.material.request.MaterialTaskWorkReportAddReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-09-9:17
 */
public interface MaterialTaskReportManager {
    /**
     * 进度报告
     */
    void progressReport(List<MultipartFile> files, MaterialTaskProgressReportAddReq progressReportAddReq);

    /**
     * 问题报告
     */
    void problemReport(List<MultipartFile> files, MaterialTaskProblemReportAddReq problemReportAddReq);

    /**
     * 工作报告
     */
    void workReport(MaterialTaskWorkReportAddReq workReportAddReq);

    /**
     * 工作报告查询
     */
    List<MaterialTaskWorkReport> selectWorkList(MaterialTaskReportListReq reportListReq);

    /**
     * 问题报告查询
     */
    List<MaterialTaskProblemReport> selectProblemList(MaterialTaskReportListReq reportListReq);

    /**
     * 进度报告查询
     */
    List<MaterialTaskProgressReport> selectProgressList(MaterialTaskReportListReq reportListReq);
}
