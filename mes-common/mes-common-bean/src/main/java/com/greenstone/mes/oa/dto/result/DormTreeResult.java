package com.greenstone.mes.oa.dto.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DormTreeResult {

    private String id;

    private String parentId;

    private String label;
    /**
     * 宿舍编号
     */
    private String dormNo;

    private String roomNo;
    /**
     * 城市
     */
    private String city;
    /**
     * 地址
     */
    private String address;

    private List<DormTreeResult> children;

}