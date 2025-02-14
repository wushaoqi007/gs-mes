package com.greenstone.mes.oa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 职级等级表
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
@TableName("oa_rank_level")
public class OaRankLevel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上级职级id
     */
    @TableField
    private Long parentId;

    /**
     * 职级名称
     */
    @TableField
    private String rankName;

    /**
     * 类型
     */
    @TableField
    private Integer type;

    /**
     * 等级
     */
    @TableField
    private String level;

    /**
     * 部门ID
     */
    @TableField
    private Long deptId;

    /**
     * 排序
     */
    @TableField
    private Integer orderNum;

}
