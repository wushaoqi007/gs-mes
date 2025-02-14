package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "sys_param_data")
public class ParamDataDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1462109838119390163L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String parentId;
    private String paramType;
    private String paramValue1;
    private String paramValue2;
    private String paramValue3;
    private Integer orderNum;
    private String remark;

}
