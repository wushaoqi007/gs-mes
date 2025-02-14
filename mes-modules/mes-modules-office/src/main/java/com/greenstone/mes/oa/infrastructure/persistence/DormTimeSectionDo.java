package com.greenstone.mes.oa.infrastructure.persistence;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.oa.enums.DormMemberOperation;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 住宿时段表;
 *
 * @author gu_renkai
 * @date 2023-4-23
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("oa_dorm_timesection")
public class DormTimeSectionDo extends BaseEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 宿舍编号
     */
    private String dormNo;
    /**
     * 床位号
     */
    private String bedNo;
    /**
     * 员工id
     */
    private Long employeeId;
    /**
     * 员工姓名
     */
    private String employeeName;
    /**
     * 入住操作
     */
    private DormMemberOperation inOperation;
    /**
     * 入住时间
     */
    private LocalDateTime inTime;
    /**
     * 离开操作
     */
    private DormMemberOperation outOperation;
    /**
     * 离开时间
     */
    private LocalDateTime outTime;

}