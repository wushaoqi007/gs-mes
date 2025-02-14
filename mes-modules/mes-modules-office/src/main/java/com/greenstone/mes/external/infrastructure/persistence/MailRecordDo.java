package com.greenstone.mes.external.infrastructure.persistence;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("mail_record")
public class MailRecordDo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String subject;

    private String mailJson;

    private boolean success;

    private LocalDateTime createTime;

    private String createBy;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
