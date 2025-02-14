package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.oa.application.service.OaRankLevelService;
import com.greenstone.mes.oa.domain.OaRankLevel;
import com.greenstone.mes.oa.infrastructure.mapper.OaRankLevelMapper;
import com.greenstone.mes.oa.request.OaRankLevelAddReq;
import com.greenstone.mes.oa.request.OaRankLevelEditReq;
import com.greenstone.mes.oa.response.OaRankLevelResp;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-05-31-14:31
 */
@Service
public class OaRankLevelServiceImpl extends ServiceImpl<OaRankLevelMapper, OaRankLevel> implements OaRankLevelService {

    @Override
    public void addRankLevel(OaRankLevelAddReq oaRankLevelAddReq) {
        duplicatedCheck(OaRankLevel.builder().rankName(oaRankLevelAddReq.getRankName()).build(), "已存在相同的职级名称");

        OaRankLevel oaRankLevel = OaRankLevel.builder().
                rankName(oaRankLevelAddReq.getRankName()).
                level(oaRankLevelAddReq.getLevel()).
                type(oaRankLevelAddReq.getType()).
                deptId(oaRankLevelAddReq.getDeptId()).
                orderNum(oaRankLevelAddReq.getOrderNum()).build();
        if (oaRankLevelAddReq.getParentId() == null) {
            oaRankLevel.setParentId(0L);
        } else {
            oaRankLevel.setParentId(oaRankLevelAddReq.getParentId());
        }
        save(oaRankLevel);
    }

    @Override
    public void updateRankLevel(OaRankLevelEditReq oaRankLevelEditReq) {
        OaRankLevel build = OaRankLevel.builder().rankName(oaRankLevelEditReq.getRankName()).build();
        QueryWrapper<OaRankLevel> queryWrapper = new QueryWrapper<>(build);
        queryWrapper.ne("id", oaRankLevelEditReq.getId());
        List<OaRankLevel> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new ServiceException("已存在相同的职级名称");
        }

        OaRankLevel oaRankLevel = OaRankLevel.builder().id(oaRankLevelEditReq.getId()).
                rankName(oaRankLevelEditReq.getRankName()).
                level(oaRankLevelEditReq.getLevel()).
                type(oaRankLevelEditReq.getType()).
                deptId(oaRankLevelEditReq.getDeptId()).
                orderNum(oaRankLevelEditReq.getOrderNum()).build();
        updateById(oaRankLevel);
    }

    @Override
    public List<OaRankLevelResp> selectRankLevelList(OaRankLevel oaRankLevel) {
        QueryWrapper<OaRankLevel> queryWrapper = new QueryWrapper<>(oaRankLevel);
        List<OaRankLevel> list = list(queryWrapper);
        List<OaRankLevelResp> respList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (OaRankLevel level : list) {
                OaRankLevelResp build = OaRankLevelResp.builder().id(level.getId()).parentId(level.getParentId()).
                        rankName(level.getRankName()).
                        type(level.getType()).
                        level(level.getLevel()).
                        deptId(level.getDeptId()).
                        orderNum(level.getOrderNum()).build();
                respList.add(build);
            }
            respList.sort(Comparator.comparingInt(OaRankLevelResp::getOrderNum));
        }
        return respList;
    }

    @Override
    public List<OaRankLevelResp> buildRankLevelTreeSelect(List<OaRankLevelResp> list) {
        List<OaRankLevelResp> returnList = new ArrayList<OaRankLevelResp>();
        List<Long> tempList = new ArrayList<Long>();
        for (OaRankLevelResp rankLevel : list) {
            tempList.add(rankLevel.getId());
        }
        for (Iterator<OaRankLevelResp> iterator = list.iterator(); iterator.hasNext(); ) {
            OaRankLevelResp rankLevel = (OaRankLevelResp) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(rankLevel.getParentId())) {
                recursionFn(list, rankLevel);
                returnList.add(rankLevel);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }

    @Override
    public OaRankLevel getRecordDetail(Long recordId) {
        QueryWrapper<OaRankLevel> queryWrapper = new QueryWrapper<>(OaRankLevel.builder().id(recordId).build());

        return getOneOnly(queryWrapper);
    }

    @Override
    public boolean deleteRankLevelById(Long id) {
        OaRankLevel recordDetail = getRecordDetail(id);
        if (recordDetail == null) {
            throw new ServiceException("未查询到职级，id为：" + id);
        }
        if (recordDetail.getType() != null && recordDetail.getType() == 0) {
            // 包含子级
            QueryWrapper<OaRankLevel> queryWrapper1 = new QueryWrapper<>(OaRankLevel.builder().parentId(id).build());
            List<OaRankLevel> oaRankLevels = list(queryWrapper1);
            if (CollectionUtil.isNotEmpty(oaRankLevels)) {
                for (OaRankLevel oaRankLevel : oaRankLevels) {
                    if (oaRankLevel.getType() != null && oaRankLevel.getType() == 0) {
                        // 子职级仍旧是职级分类，递归
                        deleteRankLevelById(oaRankLevel.getId());
                    } else {
                        removeById(id);
                    }
                }
            }
        }

        return removeById(id);
    }


    /**
     * 递归列表
     */
    private void recursionFn(List<OaRankLevelResp> list, OaRankLevelResp t) {
        // 得到子节点列表
        List<OaRankLevelResp> childList = getChildList(list, t);
        t.setChildren(childList);
        for (OaRankLevelResp tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<OaRankLevelResp> getChildList(List<OaRankLevelResp> list, OaRankLevelResp t) {
        List<OaRankLevelResp> tlist = new ArrayList<OaRankLevelResp>();
        Iterator<OaRankLevelResp> it = list.iterator();
        while (it.hasNext()) {
            OaRankLevelResp n = (OaRankLevelResp) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<OaRankLevelResp> list, OaRankLevelResp t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}
