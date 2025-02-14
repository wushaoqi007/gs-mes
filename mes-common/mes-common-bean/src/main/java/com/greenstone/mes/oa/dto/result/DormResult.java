package com.greenstone.mes.oa.dto.result;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.oa.enums.DormMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DormResult {
    /**
     * 宿舍编号
     */
    private String dormNo;
    /**
     * 城市
     */
    private String city;
    /**
     * 地址
     */
    private String address;
    /**
     * 房间号
     */
    private String roomNo;
    /**
     * 床位数
     */
    private Integer bedNumber;
    /**
     * 空闲床位数
     */
    private Integer freeBedNumber;
    /**
     * 负责人id
     */
    private Long manageBy;
    /**
     * 负责人姓名
     */
    private String manageByName;

    public List<DormMember> members;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class DormMember {
        /**
         * id
         */
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
         * 状态
         */
        private DormMemberStatus status;
        /**
         * 入住时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH")
        private LocalDateTime inTime;
        /**
         * 本人电话
         */
        private String telephone;
        /**
         * 紧急电话
         */
        private String urgentTel;
    }

}