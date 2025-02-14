package com.greenstone.mes.wxcp.infrastructure.persistence;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoAutoStart
@TableName("wx_process_control")
public class ProcessControlPo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String templateId;
    private String controlId;
    private String control;
    private String type;
    private String mode;
    private String attrName;
    private String createTime;
    private Boolean required;

}
