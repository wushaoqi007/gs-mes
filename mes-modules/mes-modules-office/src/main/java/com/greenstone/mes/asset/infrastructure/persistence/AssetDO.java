package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.*;
import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/3 8:44
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "asset")
public class AssetDO extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    private String barCode;

    private Long parentId;

    private Long typeId;

    private String typeCode;

    private String typeName;

    private String typeHierarchy;

    private String name;

    private String sn;

    private String specification;

    private String fileNumber;

    private LocalDate purchasedDate;

    private AssetState state;

    private String location;

    private String unit;

    private Long receivedId;

    private String receivedBy;

    private LocalDateTime receivedTime;

    private String billSn;

    private String remark;

    /**
     * 删除标志
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
