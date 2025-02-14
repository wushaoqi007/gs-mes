package com.greenstone.mes.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @Date 2022/8/8 15:43
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartReceiveDto {

    private List<PartReceiveDetail> partReceiveDetailList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PartReceiveDetail {

        /**
         * 机加工单号
         */
        private String partOrderCode;

        /**
         * 组件编号
         */
        private String componentCode;

        /**
         * 零件编号
         */
        private String partCode;

        /**
         * 零件版本
         */
        private String partVersion;

        /**
         * 接收数量
         */
        private Long receiveNumber;

    }

}
