package com.greenstone.mes.oa.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Dorm {
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
     * 负责人id
     */
    private Long manageBy;
    /**
     * 负责人姓名
     */
    private String manageByName;

    public List<DormMember> members;

}