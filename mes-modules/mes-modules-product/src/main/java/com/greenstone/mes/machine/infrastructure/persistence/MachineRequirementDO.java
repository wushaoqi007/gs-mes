package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.table.TablePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:12
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName(value = "machine_requirement")
public class MachineRequirementDO extends TablePo {

    @TableField("is_checked")
    private Boolean checked;
    private String projectCode;
    private Long applyById;
    private LocalDateTime applyTime;
    private LocalDateTime confirmTime;
    private Long confirmBy;
    private String remark;
    private LocalDateTime receiveDeadline;
    private String title;
    private String content;
    private String approvers;
    private String copyTo;

    private Integer mailStatus;
    private String mailMsg;

}
