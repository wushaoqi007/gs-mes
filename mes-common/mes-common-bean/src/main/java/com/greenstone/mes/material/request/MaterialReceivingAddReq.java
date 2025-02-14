
package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingAddReq {


    /**
     * 项目
     */
    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;


    /**
     * 组件列表
     */
    private List<BomInfo> bomList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class BomInfo {
        /**
         * bom的id
         */
        private Long bomId;

        /**
         * bom的名称
         */
        private String name;
    }

    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;


}
