package com.greenstone.mes.material.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-03-31-11:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksheetPlaceOrderR {
    private String partCode;
    private String partVersion;
    private String partName;
    private Integer placeOrderNum;
    private Integer receiveNum;
    private String projectCode;
    private String designer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;
}
