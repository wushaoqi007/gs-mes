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
 * @date 2023/2/6 9:23
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_requisition_detail")
public class AssetReqsDetailDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 7704710342964140566L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String serialNo;

    @TableField
    private String barCode;

}
