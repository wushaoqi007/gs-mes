package com.greenstone.mes.external.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.greenstone.mes.external.infrastructure.enums.CopyHandleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProcessCopy {
    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 序列号
     */
    private String serialNo;
    /**
     * 抄送人id
     */
    private Long userId;
    /**
     * 表单类型
     */
    private String billType;
    /**
     * 处理状态
     */
    private CopyHandleStatus handleStatus;
    /**
     * 发起人id
     */
    private Long appliedBy;
    /**
     * 发起人姓名
     */
    private String appliedByName;
    /**
     * 发起时间
     */
    private LocalDateTime appliedTime;

}