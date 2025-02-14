package com.greenstone.mes.reimbursement.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2024-01-09-10:59
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("reimbursement_application_attachment")
public class ReimbursementAppAttachmentDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -884542497389590166L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private String applicationDetailId;
    private String attachmentType;
    private String name;
    private String path;
}
