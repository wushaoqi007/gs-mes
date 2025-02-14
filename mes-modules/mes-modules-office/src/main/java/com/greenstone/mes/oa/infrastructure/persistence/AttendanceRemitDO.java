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
@TableName("oa_attendance_remit")
public class AttendanceRemitDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4085654782690016138L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("wx_cp_id")
    private String cpId;
    @TableField("wx_user_id")
    private String userId;
    @TableField
    private Date day;
    @TableField
    private Long time;
    @TableField
    private Integer checkinType;
    @TableField
    private Integer remitType;

}
