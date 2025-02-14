
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
@TableName(value = "machine_calculate_history")
public class MachineCalculateHistoryDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4286029026301835159L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String calculateDetailId;
    private String calculateSerialNo;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Double calculatePrice;
    private String calculateBy;
    private Long calculateById;
    private String calculateJson;
    private LocalDateTime calculateTime;
}
