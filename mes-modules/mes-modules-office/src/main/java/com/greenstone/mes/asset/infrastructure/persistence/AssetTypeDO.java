package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 资产类别持久化对象
 *
 * @author gu_renkai
 * @date 2023/1/30 14:53
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_type")
public class AssetTypeDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 3964299332883279421L;

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
