package com.greenstone.mes.common.param;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("common_param")
public class CommonParam {
    @TableId
    private String field;

    private String clazz;

    private String value;

    private String comment;

}
