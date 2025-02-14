package com.greenstone.mes.oa.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2023-06-20-11:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxMessageUser {
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
