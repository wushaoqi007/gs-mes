package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 请假表
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
@TableName("oa_wx_leave")
public class ApprovalVacationDO extends BaseEntity {
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
     * 请假类型
     */
    @TableField
    private String type;

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
     * 请假事由
     */
    @TableField
    private String reason;

    /**
     * 请假时长
     */
    @TableField
    private Long length;

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
