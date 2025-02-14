package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 临时白班变更登记表
 *
 * @author wushaoqi
 * @date 2022-06-29-9:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("oa_wx_temporary_change")
public class ApprovalTemporaryChangeDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业ID
     */
    @TableField
    private String cpId;

    /**
     * 审批编号
     */
    @TableField
    private String spNo;

    /**
     * 开始时间
     */
    @TableField
    private Long startTime;

    /**
     * 结束时间
     */
    @TableField
    private Long endTime;

    /**
     * 变更事由
     */
    @TableField
    private String reason;

    /**
     * 状态
     */
    @TableField
    private Integer spStatus;

    /**
     * 申请人ID
     */
    @TableField
    private String userId;

    /**
     * 姓名
     */
    @TableField
    private String userName;


    /**
     * 申请时间
     */
    @TableField
    private Long applyTime;
}
