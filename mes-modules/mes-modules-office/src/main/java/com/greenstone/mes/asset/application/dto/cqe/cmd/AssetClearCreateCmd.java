package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssetClearCreateCmd {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clearTime;

    private String remark;

    private List<ClearAsset> assets;

    @Data
    public static class ClearAsset {

        private String barCode;

    }

}
