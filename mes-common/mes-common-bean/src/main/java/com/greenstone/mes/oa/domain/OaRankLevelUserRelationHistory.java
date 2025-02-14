package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 职级等级与人员关联
 *
 * @author wushaoqi
 * @date 2022-05-31-14:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("oa_rank_level_user_relation_history")
public class OaRankLevelUserRelationHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 职级id
     */
    @TableField
    private Long rankId;

    /**
     * 人员id
     */
    @TableField
    private Long userId;

    /**
     * 评级日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date gradeTime;
}
