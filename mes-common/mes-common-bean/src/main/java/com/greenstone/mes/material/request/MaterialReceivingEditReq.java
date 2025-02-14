
package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingEditReq {


    @NotNull(message = "ID不为空")
    private Long id;

    /**
     * 任务状态(0待接收、1备料中、2待领料、3已完成、4已关闭)
     */
    private Integer status;

    /**
     * 备料完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date readyTime;

}
