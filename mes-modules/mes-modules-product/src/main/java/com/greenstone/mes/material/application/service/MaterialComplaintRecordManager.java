package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.domain.MaterialComplaintRecord;
import com.greenstone.mes.material.request.MaterialComplaintRecordAddReq;
import com.greenstone.mes.material.request.MaterialComplaintRecordEditReq;

import java.util.List;

/**
 * 投诉记录接口
 *
 * @author wushaoqi
 * @date 2022-10-21-14:38
 */
public interface MaterialComplaintRecordManager {
    /**
     * 新增投诉记录
     *
     * @param complaintRecordAddReq 投诉记录
     */
    void addComplaintRecord(MaterialComplaintRecordAddReq complaintRecordAddReq);

    List<MaterialComplaintRecord> selectComplaintRecordList(MaterialComplaintRecord materialComplaintRecord);

    /**
     * 获取零件投诉对应的质检记录人员
     */
    List<String> getInspectionList(Long id);

    /**
     * 投诉确认
     * @param complaintRecordEditReq
     */
    void confirmComplaint(MaterialComplaintRecordEditReq complaintRecordEditReq);

}
