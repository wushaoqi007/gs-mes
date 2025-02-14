package com.greenstone.mes.oa.domain;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 加班表
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
@TableName("oa_wx_overtime")
public class ApprovalAttendanceDO extends BaseEntity {
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
     * 加班事由
     */
    @TableField
    private String reason;

    /**
     * 加班时长
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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
