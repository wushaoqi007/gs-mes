package com.greenstone.mes.table.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.JsonTypeHandler;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "test", autoResultMap = true)
public class TestPo extends TablePo {

    private Long number;

    private String name;

    private String code;

    @TableField(typeHandler = JsonTypeHandler.class, javaType = true)
    private List<TestDetail> details;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestDetail {

        private String code;

        private String email;

    }

}
