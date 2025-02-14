package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2023-11-08-11:31
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_clear_item")
public class CesClearItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2960425030100741623L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long clearNum;
    private String warehouseCode;
}
