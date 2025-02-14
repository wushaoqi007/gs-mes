package com.greenstone.mes.form.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/3/3 16:38
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("form_field")
public class FormFieldDo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String form_id;
    private String name;
    private String label;
    private String type;
    private boolean show;
    private boolean disabled;
    private boolean supportFuzzy;
    private boolean supportQuery;
    private boolean extField;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;

}
