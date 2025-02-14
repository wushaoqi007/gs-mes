package com.greenstone.mes.oa.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceCalcCommand {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date start;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date end;

    private String cpId;

    private String userId;

    /**
     * 是否刷新缓存
     */
    private boolean refreshCache;
    /**
     * 是否快速计算
     */
    private boolean quickCalc;

}
