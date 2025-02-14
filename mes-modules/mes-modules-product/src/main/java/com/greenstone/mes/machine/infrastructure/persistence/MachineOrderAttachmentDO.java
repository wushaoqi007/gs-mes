package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_order_attachment")
public class MachineOrderAttachmentDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 申请单号
     */
    private String serialNo;
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件ID
     */
    private String path;

    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
}
