package com.greenstone.mes.table.domain.entity;

import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity extends TableEntity {

    @StreamField("数字")
    private Long number;

    @StreamField("名称")
    private String name;

    @StreamField("编号")
    private String code;

    @StreamField("明细")
    private List<TestDetail> details;

    private TestChangeReason changeReason;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestDetail extends TableEntity {

        @StreamField("编号")
        private String code;

        @StreamField("邮箱")
        private String email;

    }

}