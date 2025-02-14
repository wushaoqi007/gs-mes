package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * @author wushaoqi
 * @date 2024-09-10-10:18
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_requirement_attachment")
public class MachineRequirementAttachmentDO extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 申请单号
     */
    private String serialNo;
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件ID
     */
    private String path;
}
