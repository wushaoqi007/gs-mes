package com.greenstone.mes.oa.domain;

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
@TableName("oa_wx_schedule")
public class OaWxScheduleDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业ID
     */
    @TableField("wx_cp_id")
    private String cpId;

    /**
     * 申请人ID
     */
    @TableField("wx_user_id")
    private String userId;

    /**
     * 排班表日期
     */
    @TableField
    private String scheduleDate;

    /**
     * 打卡规则id
     */
    @TableField
    private Integer groupId;

    /**
     * 打卡规则名
     */
    @TableField
    private String groupName;


    /**
     * 当日安排班次id，班次id也可在打卡规则中查询获得
     */
    @TableField
    private Integer scheduleId;

    @TableField
    private String scheduleName;

    /**
     * 上班时间。距当天00:00的秒数
     */
    @TableField
    private Integer workSec;

    /**
     * 下班时间。距当天00:00的秒数
     */
    @TableField
    private Integer offWorkSec;

}
