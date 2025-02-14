package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.asset.infrastructure.enums.AssetHandleType;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:05
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "asset_handle_log")
public class AssetHandleLogDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8398433550392124637L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String barCode;

    @TableField
    private LocalDateTime handleTime;

    @TableField
    private AssetHandleType handleType;

    @TableField
    private Long billId;

    @TableField
    private Long handlerId;

    @TableField
    private String handlerName;

    @TableField
    private String content;

}
