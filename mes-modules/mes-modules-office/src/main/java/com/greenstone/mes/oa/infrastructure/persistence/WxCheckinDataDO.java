package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_wx_checkin_data")
public class WxCheckinDataDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3336300370273154584L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String cpId;
    private String wxUserId;
    private String groupName;
    private String checkinType;
    private String exceptionType;
    private Long checkinTime;
    private String locationTitle;
    private String locationDetail;
    private String wifiName;
    private String notes;
    private String wifiMac;
    private String mediaIds;
    private Integer lat;
    private Integer lng;
    private String deviceId;
    private Long schCheckinTime;
    private Integer groupId;
    private Integer scheduleId;
    private Integer timelineId;
}
