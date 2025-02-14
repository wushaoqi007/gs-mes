package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_receive_part_record")
public class PartReceiveRecordDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1164268059170548042L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sponsor;

    private Long sponsorId;

    private String receiveTime;

    private Integer handleNum;

    private Integer total;

    @TableField("is_finish")
    private Boolean finish;

}
