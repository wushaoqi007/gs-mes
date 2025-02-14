package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 每日考勤结果表
 *
 * @author wushaoqi
 * @date 2022-09-13-8:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("oa_daily_attendance_result")
public class OaDailyAttendanceResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企业微信用户ID
     */
    @TableField
    private String wxUserId;

    /**
     * 企业微信企业ID
     */
    @TableField
    private String wxCpId;

    /**
     * 年
     */
    @TableField
    private Integer year;

    /**
     * 月
     */
    @TableField
    private Integer month;

    /**
     * 日
     */
    @TableField
    private Integer day;

    /**
     * 星期
     */
    @TableField
    private String week;

    /**
     * 是否工作日
     */
    @TableField
    private String isWorkDay;

    /**
     * 工作时长
     */
    @TableField
    private Double workTime;

    /**
     * 出勤时间
     */
    @TableField
    private Long signInTime;

    /**
     * 退勤时间
     */
    @TableField
    private Long signOutTime;

    /**
     * 出勤打卡类型
     */
    @TableField
    private String signInType;

    /**
     * 退勤打卡类型
     */
    @TableField
    private String signOutType;

    /**
     * 是否加班
     */
    @TableField
    private String isOverTime;

    /**
     * 加班时长
     */
    @TableField
    private Double overTimeDuration;

    /**
     * 实际加班区间
     */
    @TableField
    private String overTimeActualPeriods;

    /**
     * 加班单审批区间
     */
    @TableField
    private String overTimeApprovalPeriods;

    /**
     * 是否请假
     */
    @TableField
    private String isLeave;

    /**
     * 请假时长
     */
    @TableField
    private Double leaveDuration;

    /**
     * 实际请假区间
     */
    @TableField
    private String leaveActualPeriods;

    /**
     * 请假审批区间
     */
    @TableField
    private String leaveApprovalPeriods;

    /**
     * 休息时间段
     */
    @TableField
    private String schRestTime;

    /**
     * 考勤异常
     */
    @TableField
    private String abnormalAttendance;

    /**
     * 使用迟到早退豁免次数
     */
    @TableField
    private Integer exemptionNum;
}
