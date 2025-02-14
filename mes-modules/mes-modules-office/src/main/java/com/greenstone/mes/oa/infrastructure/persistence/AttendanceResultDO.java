package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_attendance_result")
public class AttendanceResultDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6262144435225735178L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("wx_cp_id")
    private String cpId;
    @TableField("wx_user_id")
    private String userId;
    @TableField
    private Date day;
    @TableField("is_work_day")
    private Boolean workDay;
    @TableField
    private Integer shift;
    @TableField
    private String customShiftName;
    @TableField
    private Date schSignInTime;
    @TableField
    private Date schSignOutTime;
    @TableField
    private Date signInTime;
    @TableField
    private Date signOutTime;
    @TableField
    private String checkinLocation;
    @TableField
    private String checkinLocationSecond;
    @TableField
    private String checkinRemark;
    @TableField
    private String checkinRemarkSecond;
    @TableField
    private Integer checkinTimes;
    @TableField("is_trip")
    private boolean trip;
    // 单位：分钟
    @TableField
    private Integer workTime;
    // 单位：分钟
    @TableField
    private Integer extraWorkTime;
    // 单位：分钟
    @TableField
    private Integer vacationTime;
    @TableField
    private String vacationType;
    @TableField
    private Integer exceptionType;
    @TableField
    private Integer exceptionTime;
    @TableField
    private Integer lateEarlyRemitTimes;
    @TableField
    private Integer correctRemitTimes;
}
