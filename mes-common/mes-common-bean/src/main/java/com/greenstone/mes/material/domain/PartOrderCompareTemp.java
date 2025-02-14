package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/8/3 16:20
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("mat_part_order_compare_temp")
public class PartOrderCompareTemp extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private Long orderId;

    @TableField
    private Long orderDetailId;

    @TableField
    private String partType;

    @TableField
    private Boolean urgent;

    @TableField
    private String processCompany;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date processDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField
    private Date planDeadline;

    @TableField
    private Integer scannedPaperNumber;

}
