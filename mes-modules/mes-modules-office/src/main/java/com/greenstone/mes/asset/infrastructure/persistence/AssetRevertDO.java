package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:13
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_revert")
public class AssetRevertDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -890002274531780030L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String serialNo;

    @TableField
    private Long revertedId;

    @TableField
    private String revertedBy;

    @TableField
    private LocalDateTime revertedTime;

    @TableField
    private Long operatedId;

    @TableField
    private String operatedBy;

    @TableField
    private String remark;
    
}
