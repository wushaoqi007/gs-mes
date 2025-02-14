
package com.greenstone.mes.system.dto.cmd;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 仓库任务
 *
 * @author wushaoqi
 * @date 2022-11-01-8:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SysWarehouseJobEditReq {

    /**
     * 任务ID
     */
    @NotNull(message = "system.warehouse.job.id")
    private Long id;

    @NotEmpty(message = "system.warehouse.job.jobName")
    private String jobName;

    /**
     * 仓库ID
     */
    @NotNull(message = "system.warehouse.job.warehouseId")
    private Long warehouseId;

    /**
     * 是否包含子仓库
     */
    private Boolean containsChildren;

    /**
     * 超时时间
     */
    @NotNull(message = "system.warehouse.job.timeOut")
    private Integer timeout;

    /**
     * cron表达式
     */
    @NotEmpty(message = "system.warehouse.job.cron")
    private String cron;

    /**
     * 任务状态（0正常 1暂停）
     */
    @NotEmpty(message = "system.warehouse.job.status")
    private String status;

    @NotEmpty
    @Valid
    private List<SendUser> sendList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class SendUser {

        @NotNull(message = "system.warehouse.job.userId")
        private Long userId;

    }

}
