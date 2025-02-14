package com.greenstone.mes.material.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.domain.MaterialComplaintRecord;
import com.greenstone.mes.material.domain.MaterialQualityInspectionRecord;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.infrastructure.enums.FileRelationType;
import com.greenstone.mes.material.infrastructure.enums.ProblemLinkType;
import com.greenstone.mes.material.application.helper.MaterialFileHelper;
import com.greenstone.mes.material.application.service.MaterialComplaintRecordManager;
import com.greenstone.mes.material.request.MaterialComplaintRecordAddReq;
import com.greenstone.mes.material.request.MaterialComplaintRecordEditReq;
import com.greenstone.mes.material.domain.service.IMaterialComplaintRecordService;
import com.greenstone.mes.material.domain.service.IMaterialQualityInspectionRecordService;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.domain.service.WorksheetService;
import com.greenstone.mes.file.api.request.FileUploadReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2022-10-21-15:00
 */
@Slf4j
@Service
public class MaterialComplaintRecordManagerImpl implements MaterialComplaintRecordManager {

    @Autowired
    private IMaterialComplaintRecordService complaintRecordService;

    @Autowired
    private IMaterialQualityInspectionRecordService qualityInspectionRecordService;

    @Autowired
    private WorksheetDetailService worksheetDetailService;

    @Autowired
    private WorksheetService worksheetService;

    @Autowired
    private MaterialFileHelper fileHelper;

    @Override
    public void addComplaintRecord(MaterialComplaintRecordAddReq complaintRecordAddReq) {
        // 查询机加工单是否存在
        QueryWrapper<ProcessOrderDO> purchaseOrderQueryWrapper = Wrappers.query(ProcessOrderDO.builder().code(complaintRecordAddReq.getPartOrderCode()).build());
        ProcessOrderDO processOrderDO = worksheetService.getOneOnly(purchaseOrderQueryWrapper);
        if (Objects.isNull(processOrderDO)) {
            log.error("purchase order not find,part order code is:{}", complaintRecordAddReq.getPartOrderCode());
            throw new ServiceException(StrUtil.format("机加工单未找到，机加工单编号：{}", complaintRecordAddReq.getPartOrderCode()));
        }
        // 查询机加工单详情
        QueryWrapper<ProcessOrderDetailDO> detailQueryWrapper = Wrappers.query(ProcessOrderDetailDO.builder().projectCode(complaintRecordAddReq.getProjectCode())
                .processOrderId(processOrderDO.getId()).componentCode(complaintRecordAddReq.getComponentCode())
                .code(complaintRecordAddReq.getPartCode()).version(complaintRecordAddReq.getPartVersion()).build());
        ProcessOrderDetailDO processOrderDetailDO = worksheetDetailService.getOneOnly(detailQueryWrapper);
        if (Objects.isNull(processOrderDetailDO)) {
            log.info("purchase order detail not find,project code:{},component code:{},part order code :{},code/version :{}", complaintRecordAddReq.getProjectCode(), complaintRecordAddReq.getComponentCode(), complaintRecordAddReq.getPartOrderCode(), complaintRecordAddReq.getPartCode() + "/" + complaintRecordAddReq.getPartVersion());
            throw new ServiceException(StrUtil.format("机加工单详情未找到，项目代码：{},组件号：{}，机加工单编号：{},零件号/版本：{}", complaintRecordAddReq.getProjectCode(), complaintRecordAddReq.getComponentCode(), complaintRecordAddReq.getPartOrderCode(), complaintRecordAddReq.getPartCode() + "/" + complaintRecordAddReq.getPartVersion()));
        }
        // 投诉记录信息
        MaterialComplaintRecord materialComplaintRecord = MaterialComplaintRecord.builder().projectCode(complaintRecordAddReq.getProjectCode())
                .partOrderCode(complaintRecordAddReq.getPartOrderCode()).componentCode(complaintRecordAddReq.getComponentCode())
                .code(complaintRecordAddReq.getPartCode()).version(complaintRecordAddReq.getPartVersion())
                .taskId(complaintRecordAddReq.getTaskId()).status(0).provider(processOrderDetailDO.getProvider())
                .questioner(complaintRecordAddReq.getQuestioner()).questionerName(complaintRecordAddReq.getQuestionerName())
                .name(processOrderDetailDO.getName()).number(complaintRecordAddReq.getNumber()).remark(complaintRecordAddReq.getRemark()).build();
        // 通过零件信息去找投诉对应的质检员
        QueryWrapper<MaterialQualityInspectionRecord> queryWrapper = Wrappers.query(MaterialQualityInspectionRecord.builder().projectCode(complaintRecordAddReq.getProjectCode())
                .partOrderCode(complaintRecordAddReq.getPartOrderCode()).componentCode(complaintRecordAddReq.getComponentCode())
                .code(complaintRecordAddReq.getPartCode()).version(complaintRecordAddReq.getPartVersion()).build());

        List<MaterialQualityInspectionRecord> qualityInspectionRecordList = qualityInspectionRecordService.list(queryWrapper);
        if (CollUtil.isNotEmpty(qualityInspectionRecordList)) {
            // 质检记录结果按照质检员分组，仅当投诉对象一人时，没有异议，设置投诉记录中的质检员
            Map<String, List<MaterialQualityInspectionRecord>> collect = qualityInspectionRecordList.stream().collect(Collectors.groupingBy(MaterialQualityInspectionRecord::getCreateBy));
            if (collect != null && collect.size() == 1) {
                collect.forEach((name, list) -> {
                    materialComplaintRecord.setInspector(name);
                    // 关联质检记录（随机关联这个人的一条，一般一个零件只有一条质检记录）
                    materialComplaintRecord.setQualityId(list.get(0).getId());
                });
            }
        }

        // 保存投诉记录
        complaintRecordService.save(materialComplaintRecord);
        // 上传文件（PC端是文件form/data）
        if (CollectionUtil.isNotEmpty(complaintRecordAddReq.getFile())) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().fileList(complaintRecordAddReq.getFile()).relationId(materialComplaintRecord.getId()).relationType(FileRelationType.GOOD_PROBLEM.getType()).build();
            fileHelper.uploadFile(fileUploadReq);
        }
        // 上传文件（小程序端是Base64）
        if (complaintRecordAddReq.getFileBase64() != null) {
            FileUploadReq fileUploadReq = FileUploadReq.builder().baseStrList(complaintRecordAddReq.getFileBase64()).relationId(materialComplaintRecord.getId()).relationType(FileRelationType.GOOD_PROBLEM.getType()).build();
            fileHelper.uploadFile(fileUploadReq);
        }
    }

    @Override
    public List<MaterialComplaintRecord> selectComplaintRecordList(MaterialComplaintRecord search) {
        QueryWrapper<MaterialComplaintRecord> queryWrapper = Wrappers.query(MaterialComplaintRecord.builder().build());
        if (StrUtil.isNotBlank(search.getCode())) {
            queryWrapper.like("code", search.getCode());
        }
        if (StrUtil.isNotBlank(search.getQuestionerName())) {
            queryWrapper.like("questioner_name", search.getQuestionerName());
        }
        if (StrUtil.isNotBlank(search.getLiableBy())) {
            queryWrapper.like("liable_by", search.getLiableBy());
        }
        if (search.getProblemType() != null) {
            queryWrapper.eq("problem_type", search.getProblemType());
        }
        if (StrUtil.isNotBlank(search.getProvider())) {
            queryWrapper.like("provider", search.getProvider());
        }
        if (search.getStatus() != null) {
            queryWrapper.eq("status", search.getStatus());
        }
        if (search.getTaskId() != null) {
            queryWrapper.eq("task_id", search.getTaskId());
        }
        queryWrapper.orderByDesc("create_by");
        List<MaterialComplaintRecord> list = complaintRecordService.list(queryWrapper);
        // 设置附件信息
        if (CollectionUtil.isNotEmpty(list)) {
            for (MaterialComplaintRecord materialComplaintRecord : list) {
                materialComplaintRecord.setFileInfoList(fileHelper.getFileInfo(materialComplaintRecord.getId(), FileRelationType.GOOD_PROBLEM.getType()));
            }
        }
        return list;
    }

    @Override
    public List<String> getInspectionList(Long id) {
        MaterialComplaintRecord complaintRecord = complaintRecordService.getById(id);
        if (Objects.isNull(complaintRecord)) {
            log.error("complaint record not find,id is:" + id);
            throw new ServiceException("投诉记录未找到，id：" + id);
        }
        // 通过零件信息去找投诉对应的质检员
        QueryWrapper<MaterialQualityInspectionRecord> queryWrapper = Wrappers.query(MaterialQualityInspectionRecord.builder().projectCode(complaintRecord.getProjectCode())
                .partOrderCode(complaintRecord.getPartOrderCode()).componentCode(complaintRecord.getComponentCode())
                .code(complaintRecord.getCode()).version(complaintRecord.getVersion()).build());

        // 质检员列表
        List<String> inspectionList = new ArrayList<>();
        List<MaterialQualityInspectionRecord> qualityInspectionRecordList = qualityInspectionRecordService.list(queryWrapper);
        if (CollUtil.isNotEmpty(qualityInspectionRecordList)) {
            // 质检记录结果按照质检员分组
            Map<String, List<MaterialQualityInspectionRecord>> collect = qualityInspectionRecordList.stream().collect(Collectors.groupingBy(MaterialQualityInspectionRecord::getCreateBy));
            if (collect != null) {
                collect.forEach((name, list) -> {
                    inspectionList.add(name);
                });
            }
        }
        return inspectionList;
    }

    @Override
    public void confirmComplaint(MaterialComplaintRecordEditReq complaintRecordEditReq) {
        MaterialComplaintRecord complaintRecord = complaintRecordService.getById(complaintRecordEditReq.getId());
        if (Objects.isNull(complaintRecord)) {
            log.error("complaint record not find,id is:{}", complaintRecordEditReq.getId());
            throw new ServiceException(StrUtil.format("投诉记录未找到，id：{}", complaintRecordEditReq.getId()));
        }
        // 根据问题环节，查找检验记录：品检：通过零件和选择的检验人去找质检记录；其他：只通过零件去找最新的质检记录
        // 品检问题，责任人为对应的检验人；
        if (complaintRecordEditReq.getProblemType() != null && ProblemLinkType.QUALITY_INSPECTION.getType().equals(complaintRecordEditReq.getProblemType())) {
            if (StrUtil.isBlank(complaintRecordEditReq.getInspector())) {
                log.error("inspector is:{}", complaintRecordEditReq.getInspector());
                throw new ServiceException("品检问题，检验人不可为空！");
            }
            // 通过零件信息及质检员（质检记录创建人）去找质检记录
            QueryWrapper<MaterialQualityInspectionRecord> queryWrapper = Wrappers.query(MaterialQualityInspectionRecord.builder().projectCode(complaintRecord.getProjectCode())
                    .partOrderCode(complaintRecord.getPartOrderCode()).componentCode(complaintRecord.getComponentCode())
                    .code(complaintRecord.getCode()).version(complaintRecord.getVersion()).build());
            queryWrapper.eq("create_by", complaintRecordEditReq.getInspector());
            queryWrapper.orderByDesc("create_time");
            List<MaterialQualityInspectionRecord> qualityInspectionRecordList = qualityInspectionRecordService.list(queryWrapper);
            if (CollUtil.isEmpty(qualityInspectionRecordList)) {
                log.error("confirm complaint not find quality record, complaint id:{},inspector:{}", complaintRecord.getId(), complaintRecordEditReq.getInspector());
                throw new ServiceException(StrUtil.format("确认投诉时,未找到对应质检记录，投诉id:{},检查人：{}", complaintRecord.getId(), complaintRecordEditReq.getInspector()));
            }
            // 关联质检记录
            complaintRecord.setQualityId(qualityInspectionRecordList.get(0).getId());
            complaintRecord.setLiableBy(complaintRecordEditReq.getInspector());
            complaintRecord.setInspector(complaintRecordEditReq.getInspector());
        } else {
            // 装配问题，责任人为问题提出人
            if (complaintRecordEditReq.getProblemType() != null && ProblemLinkType.ASSEMBLING.getType().equals(complaintRecordEditReq.getProblemType())) {
                complaintRecord.setLiableBy(complaintRecord.getQuestionerName());
            }
            // 设计问题，责任人为对应设计，根据项目代码、机加工单号，零件号及版本查询加工单中的设计；
            if (complaintRecordEditReq.getProblemType() != null && ProblemLinkType.DESIGN.getType().equals(complaintRecordEditReq.getProblemType())) {
                // 查询机加工单是否存在
                QueryWrapper<ProcessOrderDO> purchaseOrderQueryWrapper = Wrappers.query(ProcessOrderDO.builder().code(complaintRecord.getPartOrderCode()).build());
                ProcessOrderDO processOrderDO = worksheetService.getOneOnly(purchaseOrderQueryWrapper);
                if (Objects.isNull(processOrderDO)) {
                    log.error("purchase order not find,order code is:{}", complaintRecord.getPartOrderCode());
                    throw new ServiceException(StrUtil.format("机加工单未找到，机加工单编号：{}", complaintRecord.getPartOrderCode()));
                }
                // 查询机加工单详情
                QueryWrapper<ProcessOrderDetailDO> detailQueryWrapper = Wrappers.query(ProcessOrderDetailDO.builder().projectCode(complaintRecord.getProjectCode())
                        .processOrderId(processOrderDO.getId()).componentCode(complaintRecord.getComponentCode())
                        .code(complaintRecord.getCode()).version(complaintRecord.getVersion()).build());
                ProcessOrderDetailDO processOrderDetailDO = worksheetDetailService.getOneOnly(detailQueryWrapper);
                if (Objects.isNull(processOrderDetailDO)) {
                    log.info("purchase order detail not find,project code:{},component code:{},part order code :{},code/version :{}", complaintRecord.getProjectCode(), complaintRecord.getComponentCode(), complaintRecord.getPartOrderCode(), complaintRecord.getCode() + "/" + complaintRecord.getVersion());
                    throw new ServiceException(StrUtil.format("机加工单详情未找到，无法确认。项目代码：{},组件号：{}，机加工单编号：{},零件号/版本：{}", complaintRecord.getProjectCode(), complaintRecord.getComponentCode(), complaintRecord.getPartOrderCode(), complaintRecord.getCode() + "/" + complaintRecord.getVersion()));
                }
                if (StrUtil.isBlank(processOrderDetailDO.getDesigner())) {
                    log.error("design is null,order code is:{}", complaintRecord.getPartOrderCode());
                    throw new ServiceException(StrUtil.format("该机加工单详情无设计人员，无法设置责任人为设计，机加工单编号：{}", complaintRecord.getPartOrderCode()));
                }
                complaintRecord.setLiableBy(processOrderDetailDO.getDesigner());
            }
            // 通过零件信息去找质检记录
            QueryWrapper<MaterialQualityInspectionRecord> queryWrapper = Wrappers.query(MaterialQualityInspectionRecord.builder().projectCode(complaintRecord.getProjectCode())
                    .partOrderCode(complaintRecord.getPartOrderCode()).componentCode(complaintRecord.getComponentCode())
                    .code(complaintRecord.getCode()).version(complaintRecord.getVersion()).build());
            queryWrapper.orderByDesc("create_time");
            List<MaterialQualityInspectionRecord> qualityInspectionRecordList = qualityInspectionRecordService.list(queryWrapper);
            if (CollUtil.isEmpty(qualityInspectionRecordList)) {
                log.error("confirm complaint not find quality record, complaint id:{}", complaintRecord.getId());
                throw new ServiceException(StrUtil.format("确认投诉时,未找到对应质检记录，投诉id:{}", complaintRecord.getId()));
            }
            // 关联质检记录
            complaintRecord.setQualityId(qualityInspectionRecordList.get(0).getId());
        }

        complaintRecord.setStatus(1);
        complaintRecord.setProblemType(complaintRecordEditReq.getProblemType());
        complaintRecordService.updateById(complaintRecord);
    }

}
