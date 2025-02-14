package com.greenstone.mes.ces.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-08-13:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CesReturn {
    private String id;
    private String serialNo;
    private LocalDateTime returnDate;
    private Long returnById;
    private String returnByName;
    private String returnByNo;
    private String remark;
    private List<CesReturnItem> items;
}
