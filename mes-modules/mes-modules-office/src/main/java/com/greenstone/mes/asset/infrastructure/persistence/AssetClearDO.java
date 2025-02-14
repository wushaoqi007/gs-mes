package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 资产清理单;
 *
 * @author gu_renkai
 * @date 2023-3-22
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("asset_clear")
public class AssetClearDO extends BaseEntity {
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
     * 清理人id
     */
    private Long clearBy;
    /**
     * 清理人姓名
     */
    private String clearByName;
    /**
     * 清理时间
     */
    private LocalDateTime clearTime;
    /**
     * 删除标志
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    private String remark;

}