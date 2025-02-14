package com.greenstone.mes.material.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingListResp {


    /**
     * ID
     */
    private Long id;

    /**
     * 单号
     */
    private String code;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 任务状态(0待接收、1备料中、2待领料、3已完成、4已关闭)
     */
    @TableField
    private Integer status;

    /**
     * 接收人
     */
    private String receiveBy;

    /**
     * 备料完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readyTime;

    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String createBy;

}
