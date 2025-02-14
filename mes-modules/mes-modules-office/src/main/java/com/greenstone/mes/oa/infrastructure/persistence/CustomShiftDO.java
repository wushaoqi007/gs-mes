package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wsqwork
 * @date 2024/12/12 15:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_custom_shift")
public class CustomShiftDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String dayShift;
    private String nightShift;
    private String location;
    private Double lat;
    private Double lng;
    private Double distance;
    private String dayRestTime;
    private String nightRestTime;

}
