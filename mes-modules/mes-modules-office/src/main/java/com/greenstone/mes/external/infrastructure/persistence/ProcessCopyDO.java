package com.greenstone.mes.external.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import lombok.*;

import java.util.Date;

/**
 * 单据抄送表;
 *
 * @author gu_renkai
 * @date 2023-3-13
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("flow_process_copy")
public class ProcessCopyDO extends BaseEntity {
    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 序列号
     */
    private String serialNo;
    /**
     * 抄送人id
     */
    private Long userId;
    /**
     * 表单类型
     */
    private String formId;
    /**
     * 处理状态
     */
    private CopyHandleStatus handleStatus;
    /**
     * 发起人id
     */
    private Long appliedBy;
    /**
     * 发起人姓名
     */
    private String appliedByName;
    /**
     * 发起时间
     */
    private Date appliedTime;

}