package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:57
 */
@Data
public class AssetReqsCreateCmd {

    private Long receivedId;

    private List<Asset> assets;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedTime;

    private String remark;

    @Data
    public static class Asset {

        private String barCode;

    }

}
