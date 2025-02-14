package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 资产清理明细;
 *
 * @author gu_renkai
 * @date 2023-3-22
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_clear_detail")
public class AssetClearDetailDO extends BaseEntity {
    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 单号
     */
    private String serialNo;
    /**
     * 资产编码
     */
    private String barCode;

}