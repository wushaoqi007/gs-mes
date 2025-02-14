package com.greenstone.mes.machine.domain.entity;

import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author wushaoqi
 * @date 2024-09-10-10:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineOrderAttachment extends TableEntity {
    @StreamField("附件名称")
    private String name;
    @StreamField("附件路径")
    private String path;
}
