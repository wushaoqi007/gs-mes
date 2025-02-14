package com.greenstone.mes.oa.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 设计部门项目对象 oa_design_project
 *
 * @author gu_renkai
 * @date 2022-05-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OaDesignProjectEditReq {

    @NotBlank
    private Long id;

    /**
     * 业务代码
     */
    @NotBlank
    private String projectCode;

    private String projectName;

    /**
     * 组长
     */
    @NotBlank
    private String groupLeader;

    /**
     * 设计担当
     */
    @NotBlank
    private String designer;

    /**
     * 类型
     */
    @NotNull
    private Integer projectType;

    /**
     * 阶段
     */
    @NotNull
    private Integer phase;

    /**
     * 3D造型完成日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date works3dFinishTime;

    /**
     * 方案评审日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planReviewTime;

    /**
     * 细化评审
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date detailReviewTime;

    /**
     * 2D图完成日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date works2dFinishTime;

    /**
     * 输出评审
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date outputReviewTime;

    /**
     * 纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

}