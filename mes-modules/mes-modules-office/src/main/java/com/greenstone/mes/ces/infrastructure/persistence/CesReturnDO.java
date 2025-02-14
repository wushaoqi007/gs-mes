package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-08-10:39
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_return")
public class CesReturnDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8857002598753744128L;
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private LocalDateTime returnDate;
    private Long returnById;
    private String returnByName;
    private String returnByNo;
    private String remark;
}
