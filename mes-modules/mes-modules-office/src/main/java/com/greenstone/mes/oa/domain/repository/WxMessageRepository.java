package com.greenstone.mes.oa.domain.repository;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.domain.converter.WxMsgConverter;
import com.greenstone.mes.oa.domain.entity.WxMessage;
import com.greenstone.mes.oa.infrastructure.mapper.WxMessageMapper;
import com.greenstone.mes.oa.infrastructure.mapper.WxMessageUserMapper;
import com.greenstone.mes.oa.infrastructure.persistence.WxMessageDO;
import com.greenstone.mes.oa.infrastructure.persistence.WxMessageUserDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-20-16:15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WxMessageRepository {

    private final WxMessageMapper messageMapper;
    private final WxMessageUserMapper messageUserMapper;
    private final WxMsgConverter msgConverter;

    public void add(WxMessage wxMessage) {
        WxMessageDO wxMessageDO = msgConverter.toMessageDO(wxMessage);
        messageMapper.insert(wxMessageDO);
        if (wxMessageDO.getId() != null) {
            List<WxMessageUserDO> messageUserDOList = msgConverter.toMessageUserDOS(wxMessageDO.getId(), wxMessage);
            if(CollUtil.isNotEmpty(messageUserDOList)){
                messageUserMapper.insertBatchSomeColumn(messageUserDOList);
            }
        }
    }

}
