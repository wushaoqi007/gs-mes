package com.greenstone.mes.system.infrastructure.po;

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
@TableName("sys_perm")
public class PermPo {

    @TableId(type = IdType.AUTO)
    private Long permId;

    private String permCode;

    private String permName;

    private Long parentId;

    private String permType;

    private Integer orderNum;

}
