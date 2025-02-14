package com.greenstone.mes.machine.domain.entity;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-12-14-11:27
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineSurfaceTreatmentStage {
    private String id;
    private String orderSerialNo;
    private String orderDetailId;
    private String checkSerialNo;
    private String checkDetailId;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String surfaceTreatment;
    private String stageName;
    private Integer totalStage;
    private Integer currentStage;

    public void trim() {
        List<String> trims = List.of(" ", "-", "_");
        stageName = StrUtil.trim(stageName, 0, character -> trims.contains(String.valueOf(character)));
        stageName = stageName.replaceAll("\r", "");
        stageName = stageName.replaceAll("\n", "");
    }
}
