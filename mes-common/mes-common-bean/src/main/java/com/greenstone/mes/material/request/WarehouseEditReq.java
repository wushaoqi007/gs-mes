package com.greenstone.mes.material.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 仓库配置对象 base_warehouse
 *
 * @author gu_renkai
 * @date 2022-01-21
 */
@Data
public class WarehouseEditReq {

    @NotNull(message = "id不为空")
    private Long id;

    private String name;

    @Length(max = 150, message = "地址过长")
    private String address;

    private Long parentId;

    /**
     * 阶段（1待收件；2待质检；3质检中；4合格；5待表处；6待返工；7表处中；8返工中；9良品）
     */
    @NotNull(message = "所属节点不为空")
    private Integer stage;

    /**
     * 仓库类型（0：原始仓库；1：砧板）
     */
    @NotNull(message = "仓库类型不为空")
    private Integer type;

    private String remark;

}