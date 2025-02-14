package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_application_item")
public class CesApplicationItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7157744324903831400L;

    @TableId(type = IdType.AUTO)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String itemName;
    @TableField
    private Long itemNum;
    @TableField
    private String purchaseLink;
    @TableField
    private String specification;
    @TableField
    private String picturePath;
    @TableField
    private Double unitPrice;
    @TableField
    private Double estimatedCost;
    @TableField
    private Long readyNum;
    @TableField
    private String itemCode;
    @TableField
    private String unit;
    @TableField
    private Long purchasedNum;
    @TableField
    private Long providedNum;

}
