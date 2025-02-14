package com.greenstone.mes.workflow.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flw_his_task")
public class FlwHisTaskPo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private String instanceNo;
    private String serialNo;
    private String processKey;
    private String processName;
    private Long originatorId;
    private String originator;
    private String originatorNo;
    private Integer taskStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
    private Long approverId;
    private String approver;
    private String approverNo;


}
