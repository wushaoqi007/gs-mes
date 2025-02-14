package com.greenstone.mes.table.infrastructure.utils;


import com.alibaba.fastjson.JSON;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ItemStreamUtilTest {

    @Test
    public void testFindDiffs() {
        User u1 = User.builder().name("张三").code("123").build();
        List<Card> cards1 = new ArrayList<>();
        cards1.add(Card.builder().id(1L).type("圆形").code("001").build());
        cards1.add(Card.builder().id(2L).type("方形").code("002").build());
        u1.setCards(cards1);


        User u2 = User.builder().name("李四").code("456").build();
        List<Card> cards2 = new ArrayList<>();
        cards2.add(Card.builder().id(2L).type("圆形").code("002").build());
        cards2.add(Card.builder().id(3L).type("方形").code("004").build());
        u2.setCards(cards2);

        List<ItemStream.Container> diffs = ItemStreamUtil.findDiffs(u2, u1);
        System.out.println(JSON.toJSONString(diffs));
        System.out.println("11111111111111111111111111111111111");
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User extends TableEntity {
        @StreamField("名称")
        private String name;

        @StreamField("编号")
        private String code;

        @StreamField("卡片")
        private List<Card> cards;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Card extends TableEntity {
        private Long id;

        @StreamField("类型")
        private String type;

        @StreamField("编号")
        private String code;
    }
}
