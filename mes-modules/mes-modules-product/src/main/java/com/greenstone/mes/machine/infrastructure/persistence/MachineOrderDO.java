package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.table.TablePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:12
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName(value = "machine_order")
public class MachineOrderDO extends TablePo {

    @TableField("is_special")
    private Boolean special;
    private String contractNo;
    private String provider;
    private LocalDate orderTime;
    private String remark;
}
