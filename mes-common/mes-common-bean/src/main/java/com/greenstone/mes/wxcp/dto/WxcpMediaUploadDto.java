package com.greenstone.mes.wxcp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxcpMediaUploadDto {

    @Nullable
    private String cpName;

    private String agentName;

    @Nullable
    private Long fileId;

    @Nullable
    private String fileName;

    private String filePath;

    private String url;

    @Nullable
    private String originalName;

}
