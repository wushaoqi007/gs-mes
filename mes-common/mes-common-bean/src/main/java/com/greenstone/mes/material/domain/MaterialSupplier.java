package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 供应商
 *
 * @author wushaoqi
 * @date 2022-09-26-15:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_supplier")
public class MaterialSupplier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商名称
     */
    @TableField
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

}
