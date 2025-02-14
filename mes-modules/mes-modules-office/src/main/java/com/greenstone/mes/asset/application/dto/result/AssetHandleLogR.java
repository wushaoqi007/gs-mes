package com.greenstone.mes.asset.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.asset.infrastructure.enums.AssetHandleType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:05
 */
@Data
public class AssetHandleLogR {

    private Long id;

    private String barCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    private AssetHandleType handleType;

    private Long billId;

    private Long handlerId;

    private String handlerName;

    private String content;

}
