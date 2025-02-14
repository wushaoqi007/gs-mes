package com.greenstone.mes.reimbursement.domain.converter;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplication;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplicationAttachment;
import com.greenstone.mes.reimbursement.domain.entity.ReimbursementApplicationDetail;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppAttachmentDO;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDO;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReimbursementAppConverter {
    // ReimbursementApplication
    ReimbursementAppDO toReimbursementApplicationDO(ReimbursementApplication reimbursementApplication);

    default String listToString(List<Long> list) {
        return cn.hutool.core.collection.CollUtil.join(list, ",");
    }

    ReimbursementApplication toReimbursementApplication(ReimbursementAppDO reimbursementAppDO);

    default List<Long> stringToList(String s) {
        return cn.hutool.core.util.StrUtil.split(s, ',', -1, true, Long::valueOf);
    }

    // ReimbursementApplicationAttachment
    ReimbursementAppAttachmentDO toReimbursementApplicationAttachmentDO(ReimbursementApplicationAttachment attachment);

    ReimbursementApplicationAttachment toReimbursementApplicationAttachment(ReimbursementAppAttachmentDO attachmentDO);

    List<ReimbursementAppAttachmentDO> toReimbursementApplicationAttachmentDOs(List<ReimbursementApplicationAttachment> attachments);

    List<ReimbursementApplicationAttachment> toReimbursementApplicationAttachments(List<ReimbursementAppAttachmentDO> attachmentDOs);

    // ReimbursementApplicationAttachment
    ReimbursementAppDetailDO toReimbursementApplicationDetailDO(ReimbursementApplicationDetail detail);

    ReimbursementApplicationDetail toReimbursementApplicationDetail(ReimbursementAppDetailDO detailDO);

    List<ReimbursementAppDetailDO> toReimbursementApplicationDetailDOs(List<ReimbursementApplicationDetail> details);

    List<ReimbursementApplicationDetail> toReimbursementApplicationDetails(List<ReimbursementAppDetailDO> detailDOS);

    default ReimbursementApplication toReimbursementApplication(ReimbursementAppDO reimbursementAppDO, List<ReimbursementAppDetailDO> detailDOS, List<ReimbursementAppAttachmentDO> attachmentDOs) {
        ReimbursementApplication reimbursementApplication = toReimbursementApplication(reimbursementAppDO);
        List<ReimbursementApplicationDetail> reimbursementApplicationDetails = toReimbursementApplicationDetails(detailDOS);
        List<ReimbursementApplicationAttachment> reimbursementApplicationAttachments = toReimbursementApplicationAttachments(attachmentDOs);
        if (CollUtil.isNotEmpty(reimbursementApplicationDetails) && CollUtil.isNotEmpty(reimbursementApplicationAttachments)) {
            for (ReimbursementApplicationDetail reimbursementApplicationDetail : reimbursementApplicationDetails) {
                List<ReimbursementApplicationAttachment> myAttachments = reimbursementApplicationAttachments.stream().filter(a -> a.getApplicationDetailId().equals(reimbursementApplicationDetail.getId())).collect(Collectors.toList());
                reimbursementApplicationDetail.setAttachments(myAttachments);
            }
        }
        reimbursementApplication.setDetails(reimbursementApplicationDetails);
        return reimbursementApplication;
    }
}
