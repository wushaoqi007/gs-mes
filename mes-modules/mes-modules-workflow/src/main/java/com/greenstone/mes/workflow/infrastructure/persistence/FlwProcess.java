package com.greenstone.mes.workflow.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("flw_process")
public class FlwProcess {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String processName;
    private String processKey;
    private String businessKey;
    private Integer processSource;
    private String createTime;

}
