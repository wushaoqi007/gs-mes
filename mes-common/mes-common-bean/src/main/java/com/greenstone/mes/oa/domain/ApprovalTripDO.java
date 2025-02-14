package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 出差表
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
@TableName("oa_wx_business_trip")
public class ApprovalTripDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 出差事由
     */
    @TableField
    private String reason;

    /**
     * 出差地点
     */
    @TableField
    private String location;

    /**
     * 出差时长
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
