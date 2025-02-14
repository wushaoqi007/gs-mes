package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_calculate_detail")
public class MachineCalculateDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6515814148052225724L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long partNumber;

    private Double totalPrice;
    private Double calculatePrice;
    private String calculateJson;
    private LocalDateTime calculateTime;
    private String calculateBy;
    private Long calculateById;
}
