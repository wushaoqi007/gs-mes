package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/9 10:36
 */
@Data
public class AssetRevertCreateCmd {

    private Long revertedId;

    @NotEmpty
    private List<Asset> assets;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime revertedTime;

    private String remark;

    @Data
    public static class Asset {

        private String barCode;

    }

}
