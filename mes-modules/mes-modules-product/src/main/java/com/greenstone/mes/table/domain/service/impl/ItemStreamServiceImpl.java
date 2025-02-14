package com.greenstone.mes.table.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.adapter.UserServiceAdapter;
import com.greenstone.mes.table.core.TableThreadLocal;
import com.greenstone.mes.table.domain.service.ItemStreamService;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.mapper.ItemStreamMapper;
import com.greenstone.mes.table.infrastructure.persistence.ItemStream;
import com.greenstone.mes.table.infrastructure.utils.ItemStreamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemStreamServiceImpl implements ItemStreamService {

    private final ItemStreamMapper itemStreamMapper;
    private final UserServiceAdapter userService;

    public <E extends TableEntity> List<ItemStream> getStreams(E e) {
        if (TableThreadLocal.getTableMeta() == null || TableThreadLocal.getTableMeta().getFunctionId() == null) {
            throw new RuntimeException("历史记录查询错误：请指定功能");
        }
        QueryWrapper<ItemStream> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        wrapper.eq("function_id", TableThreadLocal.getTableMeta().getFunctionId());
        wrapper.eq(e.getId() != null, "item_id", e.getId());
        wrapper.like(e.getSerialNo() != null, "serial_no", e.getSerialNo());
        if (e.getChangeReason() != null) {
            TableChangeReason changeReason = e.getChangeReason();
            Field[] fields = ReflectUtil.getFields(changeReason.getClass());
            for (Field field : fields) {
                Object value = ReflectUtil.getFieldValue(changeReason, field);
                if (value != null) {
                    wrapper.like("reason -> '$." + field.getName() + "'", value);
                }
            }
        }
        List<ItemStream> itemStreams = itemStreamMapper.selectList(wrapper);
        for (ItemStream itemStream : itemStreams) {
            itemStream.setCreateUser(userService.getUserById(itemStream.getCreateBy()));
        }
        return itemStreams;
    }

    @Override
    public <E extends TableEntity> void updateStream(E entity, E oldEntity) {
        List<ItemStream.Container> diffs = ItemStreamUtil.findDiffs(entity, oldEntity);
        if (CollUtil.isNotEmpty(diffs)) {
            ItemStream itemStream = ItemStream.builder().itemId(entity.getId())
                    .functionId(entity.getFunctionId())
                    .serialNo(oldEntity.getSerialNo())
                    .createTime(LocalDateTime.now())
                    .createBy(SecurityUtils.getUserId())
                    .itemAction(TableConst.Rights.UPDATE)
                    .diffs(diffs)
                    .reason(entity.getChangeReason()).build();
            itemStreamMapper.insert(itemStream);
        }

    }

}
