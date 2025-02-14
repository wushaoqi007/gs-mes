package com.greenstone.mes.workflow.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flw_his_instance")
public class FlwHisInstance {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private String instanceNo;
    private String processKey;
    private String processName;
    private String serialNo;
    private Integer instanceStatus;
    private Long originatorId;
    private String originator;
    private String originatorNo;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private String wxCpId;

}
