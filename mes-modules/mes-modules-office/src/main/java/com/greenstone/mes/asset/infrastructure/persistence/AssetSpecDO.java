package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author gu_renkai
 * @date 2023/2/2 16:30
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_specification")
public class AssetSpecDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -9071700572467302397L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String typeCode;

    @TableField
    private String templateName;

    @TableField
    private String specification;

    @TableField
    private String unit;

}
