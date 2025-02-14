package com.greenstone.mes.reimbursement.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.repository.AbstractFormDataRepository;
import com.greenstone.mes.reimbursement.application.dto.ReimbursementAppFuzzyQuery;
import com.greenstone.mes.reimbursement.domain.converter.ReimbursementAppConverter;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplicationDetail;
import com.greenstone.mes.reimbursement.infrastructure.mapper.ReimbursementApplicationAttachmentMapper;
import com.greenstone.mes.reimbursement.infrastructure.mapper.ReimbursementApplicationDetailMapper;
import com.greenstone.mes.reimbursement.infrastructure.mapper.ReimbursementApplicationMapper;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppAttachmentDO;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDO;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDetailDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ReimbursementAppRepository extends AbstractFormDataRepository<ReimbursementApplication, ReimbursementAppDO, ReimbursementApplicationMapper> {

    private final ReimbursementApplicationMapper applicationMapper;
    private final ReimbursementApplicationDetailMapper detailMapper;
    private final ReimbursementApplicationAttachmentMapper attachmentMapper;
    private final ReimbursementAppConverter converter;

    public ReimbursementAppRepository(FormHelper formHelper, ReimbursementApplicationMapper applicationMapper,
                                      ReimbursementApplicationAttachmentMapper attachmentMapper, ReimbursementApplicationDetailMapper detailMapper,
                                      ReimbursementAppConverter converter) {
        super(formHelper, applicationMapper);
        this.applicationMapper = applicationMapper;
        this.detailMapper = detailMapper;
        this.attachmentMapper = attachmentMapper;
        this.converter = converter;
    }

    public ReimbursementApplication get(String serialNo) {
        ReimbursementAppDO appDO = applicationMapper.getOneOnly(ReimbursementAppDO.builder().serialNo(serialNo).build());
        return converter.toReimbursementApplication(appDO);
    }

    public ReimbursementApplication detail(String serialNo) {
        ReimbursementAppDO appDO = applicationMapper.getOneOnly(ReimbursementAppDO.builder().serialNo(serialNo).build());
        if (Objects.isNull(appDO)) {
            throw new ServiceException(FormError.E70101);
        }
        List<ReimbursementAppDetailDO> detailDOS = detailMapper.list(ReimbursementAppDetailDO.builder().serialNo(serialNo).build());
        List<ReimbursementAppAttachmentDO> attachmentDOs = attachmentMapper.list(ReimbursementAppAttachmentDO.builder().serialNo(serialNo).build());
        return converter.toReimbursementApplication(appDO, detailDOS, attachmentDOs);
    }

    public List<ReimbursementApplication> list(ReimbursementAppFuzzyQuery fuzzyQuery) {
        QueryWrapper<ReimbursementAppDO> fuzzyQueryWrapper = new QueryWrapper<>();
        fuzzyQueryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotEmpty(fuzzyQuery.getKey()) && CollUtil.isNotEmpty(fuzzyQuery.getFields())) {
            fuzzyQueryWrapper.and(wrapper -> {
                for (String field : fuzzyQuery.getFields()) {
                    String fieldCode = StrUtil.toSymbolCase(field, '_');
                    wrapper.or().like(fieldCode, fuzzyQuery.getKey());
                }
            });
        }
        if (Objects.nonNull(fuzzyQuery.getStatus())) {
            fuzzyQueryWrapper.eq("status", fuzzyQuery.getStatus());
        }
        if (Objects.nonNull(fuzzyQuery.getSubmitById())) {
            fuzzyQueryWrapper.eq("submit_by_id", fuzzyQuery.getSubmitById());
        }
        if (Objects.nonNull(fuzzyQuery.getApprovedById())) {
            fuzzyQueryWrapper.eq("approved_by_id", fuzzyQuery.getApprovedById());
        }
        List<ReimbursementApplication> applications = new ArrayList<>();
        List<ReimbursementAppDO> applicationDOS = applicationMapper.selectList(fuzzyQueryWrapper);
        for (ReimbursementAppDO appDO : applicationDOS) {
            List<ReimbursementAppDetailDO> detailDOS = detailMapper.list(ReimbursementAppDetailDO.builder().serialNo(appDO.getSerialNo()).build());
            List<ReimbursementAppAttachmentDO> attachmentDOs = attachmentMapper.list(ReimbursementAppAttachmentDO.builder().serialNo(appDO.getSerialNo()).build());
            applications.add(converter.toReimbursementApplication(appDO, detailDOS, attachmentDOs));
        }
        return applications;
    }

    public void save(ReimbursementApplication reimbursementApplication) {
        ReimbursementAppDO reimbursementAppDO = converter.toReimbursementApplicationDO(reimbursementApplication);
        double total = 0;
        if (StrUtil.isNotEmpty(reimbursementApplication.getId())) {
            detailMapper.delete(ReimbursementAppDetailDO.builder().serialNo(reimbursementAppDO.getSerialNo()).build());
            attachmentMapper.delete(ReimbursementAppAttachmentDO.builder().serialNo(reimbursementAppDO.getSerialNo()).build());
        }
        for (ReimbursementApplicationDetail detail : reimbursementApplication.getDetails()) {
            total += detail.getAmount();
            ReimbursementAppDetailDO detailDO = converter.toReimbursementApplicationDetailDO(detail);
            detailDO.setSerialNo(reimbursementApplication.getSerialNo());
            detailMapper.insert(detailDO);

            List<ReimbursementAppAttachmentDO> attachmentDOs = converter.toReimbursementApplicationAttachmentDOs(detail.getAttachments());
            attachmentDOs.forEach(a -> {
                a.setSerialNo(reimbursementApplication.getSerialNo());
                a.setApplicationDetailId(detailDO.getId());
            });
            attachmentMapper.insertBatchSomeColumn(attachmentDOs);
        }
        reimbursementAppDO.setTotal(total);
        reimbursementApplication.setTotal(total);
        if (StrUtil.isEmpty(reimbursementApplication.getId())) {
            applicationMapper.insert(reimbursementAppDO);
        } else {
            applicationMapper.update(reimbursementAppDO, Wrappers.lambdaQuery(ReimbursementAppDO.class).eq(ReimbursementAppDO::getSerialNo, reimbursementAppDO.getSerialNo()));
        }
    }

    public void updateById(ReimbursementApplication reimbursementApplication) {
        ReimbursementAppDO reimbursementAppDO = converter.toReimbursementApplicationDO(reimbursementApplication);
        applicationMapper.update(reimbursementAppDO, Wrappers.lambdaQuery(ReimbursementAppDO.class).eq(ReimbursementAppDO::getSerialNo, reimbursementAppDO.getSerialNo()));
    }

    public void delete(List<String> serialNos) {
        LambdaQueryWrapper<ReimbursementAppDO> wrapper = Wrappers.lambdaQuery(ReimbursementAppDO.class).in(ReimbursementAppDO::getSerialNo, serialNos);
        applicationMapper.delete(wrapper);
        LambdaQueryWrapper<ReimbursementAppDetailDO> detailWrapper = Wrappers.lambdaQuery(ReimbursementAppDetailDO.class).in(ReimbursementAppDetailDO::getSerialNo, serialNos);
        detailMapper.delete(detailWrapper);
        LambdaQueryWrapper<ReimbursementAppAttachmentDO> attachmentWrapper = Wrappers.lambdaQuery(ReimbursementAppAttachmentDO.class).in(ReimbursementAppAttachmentDO::getSerialNo, serialNos);
        attachmentMapper.delete(attachmentWrapper);
    }

    public void changeStatus(List<String> serialNos, ProcessStatus status) {
        LambdaUpdateWrapper<ReimbursementAppDO> wrapper = Wrappers.lambdaUpdate(ReimbursementAppDO.class).set(ReimbursementAppDO::getStatus, status)
                .in(ReimbursementAppDO::getSerialNo, serialNos);
        applicationMapper.update(wrapper);
    }

    public void changeStatus(String serialNo, ProcessStatus status) {
        LambdaUpdateWrapper<ReimbursementAppDO> wrapper = Wrappers.lambdaUpdate(ReimbursementAppDO.class).set(ReimbursementAppDO::getStatus, status)
                .eq(ReimbursementAppDO::getSerialNo, serialNo);
        applicationMapper.update(wrapper);
    }

}
