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
@TableName(value = "ces_return_item")
public class CesReturnItemDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2138903514161781215L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String requisitionItemId;
    private String serialNo;
    private String requisitionSerialNo;
    private String itemName;
    private String itemCode;
    private String typeName;
    private String specification;
    private Long returnNum;
    private Long lossNum;
    private String warehouseCode;
}
