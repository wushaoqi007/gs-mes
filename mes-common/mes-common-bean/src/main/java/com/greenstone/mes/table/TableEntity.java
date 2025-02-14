package com.greenstone.mes.table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @WorkflowField
    private Long id;

    /**
     * 序列号
     */
    @WorkflowField
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
    private Long createBy;

    /**
     * 创建人对象
     */
    private User createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 提交人
     */
    private Long submitBy;

    /**
     * 提交人对象
     */
    private User submitUser;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新人对象
     */
    private User updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 流程发起人对象
     */
    private User processStartUser;

    /**
     * 流程启动时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processStartTime;

    /**
     * 流程结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processEndTime;

    /* ---------------------------------------------------------- */

    private Boolean editable;

    /**
     * 功能id
     */
    private Long functionId;

    /**
     * 查询参数
     */
    private Map<String, Object> params;

    /**
     * 变更原因
     */
    private TableChangeReason changeReason;

    @Override
    public boolean equals(Object o) {
        return o instanceof TableEntity;
    }

}
