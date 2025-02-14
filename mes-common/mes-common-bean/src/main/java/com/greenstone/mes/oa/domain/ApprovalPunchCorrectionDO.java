package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 打卡补卡表
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
@TableName("oa_wx_punch_correction")
public class ApprovalPunchCorrectionDO extends BaseEntity {
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
     * 补卡时间
     */
    @TableField
    private Long correctionTime;

    /**
     * 加班事由
     */
    @TableField
    private String reason;

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


    /**
     * 重算次数
     */
    @TableField
    private Integer recalculate;
}
