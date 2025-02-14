package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 企业微信消息记录
 *
 * @author wushaoqi
 * @date 2023-06-19-13:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_wx_message_user")
public class WxMessageUserDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1503452920763770480L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 消息id
     */
    @TableField
    private Long messageId;
    /**
     * 企业微信用户ID
     */
    @TableField
    private String wxUserId;
    /**
     * 系统用户ID
     */
    @TableField
    private Long sysUserId;
}
