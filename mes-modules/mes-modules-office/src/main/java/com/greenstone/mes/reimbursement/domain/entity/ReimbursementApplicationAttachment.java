package com.greenstone.mes.reimbursement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReimbursementApplicationAttachment {
    private String id;
    private String serialNo;
    private String applicationDetailId;
    private String attachmentType;
    private String name;
    private String path;
}
