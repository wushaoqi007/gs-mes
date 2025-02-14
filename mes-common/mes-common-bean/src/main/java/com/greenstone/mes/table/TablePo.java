package com.greenstone.mes.table;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(autoResultMap = true)
public class TablePo {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 数据锁定
     */
    private Boolean locked;

    /**
     * 数据状态
     */
    private Integer dataStatus;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 提交人
     */
    private Long submitBy;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 流程实例编号
     */
    private String processInstanceNo;

    /**
     * 流程状态
     */
    private Integer processStatus;

    /**
     * 流程进展
     */
    private String processProgress;

    /**
     * 流程发起人
     */
    private Long processStartBy;

    /**
     * 流程启动时间
     */
    private LocalDateTime processStartTime;

    /**
     * 流程结束时间
     */
    private LocalDateTime processEndTime;

}
