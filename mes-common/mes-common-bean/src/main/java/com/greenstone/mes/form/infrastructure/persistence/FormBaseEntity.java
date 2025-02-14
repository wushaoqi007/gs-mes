package com.greenstone.mes.form.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greenstone.mes.external.enums.ProcessStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity基类
 *
 * @author gurenkai
 */
public class FormBaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 状态
     */
    private ProcessStatus status;

    /**
     * 自定义数据
     */
    private Object dataJson;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 删除标志
     */
    private boolean deleted;

    /**
     * 创建者id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建者姓名
     */
    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    /**
     * 创建者工号
     */
    @TableField(fill = FieldFill.INSERT)
    private String createByNo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新者姓名
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateByName;

    /**
     * 更新者工号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateByNo;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField(exist = false)
    @JsonIgnore
    private String remark;

}
