package com.greenstone.mes.table.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.ChangeReasonTypeHandler;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "item_stream", autoResultMap = true)
public class ItemStream {

    @TableId(type = IdType.AUTO)
    private Long streamId;

    private Long functionId;

    private Long itemId;

    private String serialNo;

    private Long createBy;

    @TableField(exist = false)
    private User createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String itemAction;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Container> diffs;

    @TableField(typeHandler = ChangeReasonTypeHandler.class)
    private TableChangeReason reason;


    public static class Container {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Table extends Container {
        @Builder.Default
        private String type = "table";

        private String label;

        private List<Row> rows;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {
        @Builder.Default
        private String type = "row";

        private Fields diffFields;

        private Fields siblingFields;

        private Serializable itemId;

        private String action;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Fields extends Container {
        @Builder.Default
        private String type = "fields";

        private String label;

        private List<Field> fields;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Field extends Container {

        @Builder.Default
        private String type = "field";

        private String label;

        private String value;

        private String oldValue;

    }

}
