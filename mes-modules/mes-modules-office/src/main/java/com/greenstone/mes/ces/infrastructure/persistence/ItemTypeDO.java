package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 物品类型
 *
 * @author wushaoqi
 * @date 2023-05-22-10:00
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("ces_item_type")
public class ItemTypeDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5669126196843042558L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String typeCode;

    @TableField
    private String typeName;

    @TableField
    private String parentTypeCode;

    @TableField
    private String idHierarchy;

    @TableField
    private String nameHierarchy;
}
