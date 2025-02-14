package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.system.api.domain.FileRecord;
import lombok.*;

import java.util.List;

/**
 * 投诉记录表
 *
 * @author wushaoqi
 * @date 2022-09-14-14:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_complaint_record")
public class MaterialComplaintRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 组件的编码
     */
    @TableField
    private String componentCode;

    /**
     * 机加工单编号
     */
    @TableField
    private String partOrderCode;

    /**
     * 零件号
     */
    @TableField
    private String code;

    /**
     * 版本
     */
    @TableField
    private String version;

    /**
     * 零件名称
     */
    @TableField
    private String name;

    /**
     * 有问题数量
     */
    @TableField
    private Long number;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 检查人
     */
    @TableField
    private String inspector;

    /**
     * 质检记录ID
     */
    @TableField
    private Long qualityId;

    /**
     * 责任人
     */
    @TableField
    private String liableBy;

    /**
     * 状态（0:待确认、1:已确认）
     */
    @TableField
    private Integer status;

    /**
     * 问题环节（1:设计、2:品检、3:装配）
     */
    @TableField
    private Integer problemType;

    /**
     * 任务ID
     */
    @TableField
    private Long taskId;

    /**
     * 提出人
     */
    @TableField
    private Long questioner;

    /**
     * 提问人姓名
     */
    @TableField
    private String questionerName;

    /**
     * 加工商
     */
    @TableField
    private String provider;

    /**
     * 附件信息
     */
    @TableField(exist=false)
    private List<FileRecord> fileInfoList;

}
