package com.greenstone.mes.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 自定义表格默认配置
 *
 * @author wushaoqi
 * @date 2022-10-31-8:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("sys_custom_table_default")
public class SysCustomTableDefault extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String tableName;

    @TableField
    private String columnName;

    @TableField
    private String columnNameCn;

    @TableField
    private Integer width;

    @TableField
    private Boolean isShow;

    @TableField
    private Boolean isFilter;

    @TableField
    private Boolean isNecessary;

    @TableField
    private Integer sort;
}
