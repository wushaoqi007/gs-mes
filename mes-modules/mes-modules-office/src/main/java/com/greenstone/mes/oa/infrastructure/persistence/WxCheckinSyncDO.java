package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_wx_checkin_sync")
public class WxCheckinSyncDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2230496639737230669L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String wxCpId;

    private String wxUserId;

    private Long beginSec;

    private Long endSec;

}
