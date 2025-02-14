package com.greenstone.mes.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2023/2/6 11:02
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sys_serial_no")
public class SerialNo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String type;

    @TableField
    private String prefix;

    @TableField
    private Long number;
}
