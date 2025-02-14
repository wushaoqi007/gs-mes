package com.greenstone.mes.material.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 在制项目零件进度
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatPartsProgress {


    private Long id;

    private Long worksheetDetailId;

    private String customerName;

    private String customerShortName;

    private Integer deliverPartNum;
    private Integer deliverPaperNum;
    private String deliverRate;
    private Integer finishedPartNum;
    private Integer finishedPaperNum;
    private String finishedRate;

    private String projectCode;
    private String componentCode;
    private String componentName;
    private Date uploadTime;
    private Date planTime;
    private Integer partNum;
    private Integer paperNum;
    private String remark;
}
