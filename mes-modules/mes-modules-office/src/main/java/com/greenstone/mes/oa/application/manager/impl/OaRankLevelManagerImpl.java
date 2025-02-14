package com.greenstone.mes.oa.application.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.manager.OaRankLevelManager;
import com.greenstone.mes.oa.application.service.OaRankLevelService;
import com.greenstone.mes.oa.application.service.OaRankLevelUserRelationHistoryService;
import com.greenstone.mes.oa.application.service.OaRankLevelUserRelationService;
import com.greenstone.mes.oa.domain.OaRankLevel;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelation;
import com.greenstone.mes.oa.domain.OaRankLevelUserRelationHistory;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationAddReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationEditReq;
import com.greenstone.mes.oa.request.OaRankLevelUserRelationListReq;
import com.greenstone.mes.oa.response.OaRankLevelExportDataListResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2022-06-01-8:30
 */
@Slf4j
@Service
public class OaRankLevelManagerImpl implements OaRankLevelManager {

    @Autowired
    private OaRankLevelService oaRankLevelService;

    @Autowired
    private OaRankLevelUserRelationService oaRankLevelUserRelationService;

    @Autowired
    private OaRankLevelUserRelationHistoryService oaRankLevelUserRelationHistoryService;

    @Override
    public boolean checkRankLevelExistRelation(Long id) {
        int count = 0;
        OaRankLevel recordDetail = oaRankLevelService.getRecordDetail(id);
        if (recordDetail == null) {
            throw new ServiceException("未查询到职级，id为：" + id);
        }
        if (recordDetail.getType() != null && recordDetail.getType() == 0) {
            QueryWrapper<OaRankLevel> queryWrapper1 = new QueryWrapper<>(OaRankLevel.builder().parentId(id).build());
            List<OaRankLevel> oaRankLevels = oaRankLevelService.list(queryWrapper1);
            if (CollectionUtil.isNotEmpty(oaRankLevels)) {
                for (OaRankLevel oaRankLevel : oaRankLevels) {
                    if (oaRankLevel.getType() != null && oaRankLevel.getType() == 0) {
                        // 子职级仍旧是职级分类，递归
                        checkRankLevelExistRelation(oaRankLevel.getId());
                    } else {
                        // 子职级是职级，判断人员职级关系
                        if (checkExistRelation(oaRankLevel.getId())) {
                            count++;
                        }
                    }
                }

            }
        }

        if (checkExistRelation(id)) {
            count++;
        }
        // 判断所有层级是否都不包含人员职级关系
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 去重
     */
    private ArrayList<OaRankLevelUserRelationAddReq.Relation> removeDuplicateUser(List<OaRankLevelUserRelationAddReq.Relation> list) {
        Set<OaRankLevelUserRelationAddReq.Relation> set = new TreeSet<OaRankLevelUserRelationAddReq.Relation>(new Comparator<OaRankLevelUserRelationAddReq.Relation>() {
            @Override
            public int compare(OaRankLevelUserRelationAddReq.Relation o1, OaRankLevelUserRelationAddReq.Relation o2) {
                //字符串,则按照asicc码升序排列
                return o1.getUserId().compareTo(o2.getUserId());
            }
        });
        set.addAll(list);
        return new ArrayList<OaRankLevelUserRelationAddReq.Relation>(set);
    }

    @Override
    public void addRankLevelUserRelation(OaRankLevelUserRelationAddReq oaRankLevelUserRelationAddReq) {
        if (CollectionUtil.isEmpty(oaRankLevelUserRelationAddReq.getList())) {
            throw new ServiceException("不可空添加：" + oaRankLevelUserRelationAddReq);
        }
        // 去除重复人员添加
        ArrayList<OaRankLevelUserRelationAddReq.Relation> relations = removeDuplicateUser(oaRankLevelUserRelationAddReq.getList());
        for (OaRankLevelUserRelationAddReq.Relation relation : relations) {
            OaRankLevelUserRelation oneOnly = oaRankLevelUserRelationService.getOneOnly(OaRankLevelUserRelation.builder().userId(relation.getUserId()).build());
            if (oneOnly != null) {
                // 修改并添加到历史
                oaRankLevelUserRelationHistoryService.save(OaRankLevelUserRelationHistory.builder().gradeTime(oneOnly.getGradeTime()).
                        rankId(oneOnly.getRankId()).userId(oneOnly.getUserId()).build());
                OaRankLevelUserRelation build = OaRankLevelUserRelation.builder().
                        id(oneOnly.getId()).
                        userId(relation.getUserId()).
                        rankId(relation.getRankId()).
                        gradeTime(new Date()).build();
                oaRankLevelUserRelationService.updateById(build);

            } else {
                // 新增
                OaRankLevelUserRelation build = OaRankLevelUserRelation.builder().
                        userId(relation.getUserId()).
                        rankId(relation.getRankId()).
                        gradeTime(new Date()).build();
                oaRankLevelUserRelationService.save(build);
            }
        }

    }

    @Override
    public boolean deleteRankLevelUserRelationById(Long id) {
        // 删除前先添加到历史表中
        OaRankLevelUserRelation info = oaRankLevelUserRelationService.getById(id);
        if (info == null) {
            throw new ServiceException("未查询到职级人员关系，id为：" + id);
        }
        OaRankLevelUserRelationHistory build = OaRankLevelUserRelationHistory.builder().userId(info.getUserId()).
                rankId(info.getRankId()).gradeTime(info.getGradeTime()).build();
        oaRankLevelUserRelationHistoryService.save(build);

        return oaRankLevelUserRelationService.removeById(id);
    }

    @Override
    public boolean deleteRankLevelById(Long id) {
        OaRankLevel recordDetail = oaRankLevelService.getRecordDetail(id);
        if (recordDetail == null) {
            throw new ServiceException("未查询到职级，id为：" + id);
        }
        // 查询是否有子职级
        QueryWrapper<OaRankLevel> queryWrapper1 = new QueryWrapper<>(OaRankLevel.builder().parentId(id).build());
        List<OaRankLevel> oaRankLevels = oaRankLevelService.list(queryWrapper1);
        if (CollectionUtil.isNotEmpty(oaRankLevels)) {
            for (OaRankLevel oaRankLevel : oaRankLevels) {
                // 子职级仍旧是职级分类，递归
                deleteRankLevelById(oaRankLevel.getId());
            }
        }

        return oaRankLevelService.removeById(id);
    }

    @Override
    public void updateRankLevelUserRelation(OaRankLevelUserRelationEditReq oaRankLevelUserRelationEditReq) {
        OaRankLevelUserRelation oneOnly = oaRankLevelUserRelationService.getById(oaRankLevelUserRelationEditReq.getId());
        if (oneOnly == null) {
            throw new ServiceException("未查询到职级人员关系，id为：" + oaRankLevelUserRelationEditReq.getId());
        }
        // 修改并添加到历史
        oaRankLevelUserRelationHistoryService.save(OaRankLevelUserRelationHistory.builder().gradeTime(oneOnly.getGradeTime()).
                rankId(oneOnly.getRankId()).userId(oneOnly.getUserId()).build());
        OaRankLevelUserRelation build = OaRankLevelUserRelation.builder().
                id(oneOnly.getId()).
                userId(oaRankLevelUserRelationEditReq.getUserId()).
                rankId(oaRankLevelUserRelationEditReq.getRankId()).
                gradeTime(new Date()).build();
        oaRankLevelUserRelationService.updateById(build);
    }

    @Override
    public XSSFWorkbook exportRankLevel(OaRankLevelUserRelationListReq rankLevel) {
        // 查询数据源
        // 职级等级（表头）
        QueryWrapper<OaRankLevel> queryWrapper = new QueryWrapper<>(OaRankLevel.builder().deptId(rankLevel.getDeptId()).build());
        List<OaRankLevel> list = oaRankLevelService.list(queryWrapper);
        // 顶级职级
        List<OaRankLevel> topOaRankLevel = list.stream().filter(a -> a.getType() == 0).collect(Collectors.toList());
        // 排序
        list.sort(Comparator.comparing(OaRankLevel::getOrderNum));
        topOaRankLevel.sort(Comparator.comparing(OaRankLevel::getOrderNum));

        // 职级所有关系包括历史
        List<OaRankLevelExportDataListResp> relations = oaRankLevelUserRelationHistoryService.listExportData(rankLevel);
        // 取出姓名
        List<String> nameList = relations.stream().map(OaRankLevelExportDataListResp::getUserName).distinct().collect(Collectors.toList());

        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建Sheet页
        XSSFSheet sheet = workbook.createSheet();
        // 冻结前3行
        sheet.createFreezePane(0, 3, 0, 3);
        // 表头样式
        XSSFCellStyle fontCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setBold(true);
        fontCellStyle.setFont(font);
        fontCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        // 第一行为标题
        XSSFRow rowTitle = sheet.createRow(0);
        XSSFCell cellName = rowTitle.createCell(0);
        XSSFCell cellTitle = rowTitle.createCell(1);
        cellName.setCellValue("姓名");
        cellTitle.setCellValue("技能等级");
        cellTitle.setCellStyle(fontCellStyle);

        // 第二行为顶级职级
        XSSFRow row1 = sheet.createRow(1);
        // 第三行为下级职级
        XSSFRow row2 = sheet.createRow(2);
        // 列光标
        int indexCol = 0;
        // 标记职级id对应的列光标，表头内容是动态的，所以要标记
        Map<Long, Integer> rankIdToIndexCol = new HashMap<>();
        // 列数为职级等级数量
        for (int j = 0; j < topOaRankLevel.size(); j++) {
            // 找到顶级职级的子级
            int finalJ = j;
            List<OaRankLevel> child = list.stream().filter(a -> a.getParentId().equals(topOaRankLevel.get(finalJ).getId())).collect(Collectors.toList());
            // 调整子级顺序
            child.sort(Comparator.comparing(OaRankLevel::getOrderNum));

            int firstCol = indexCol + 1;
            int lastCol = indexCol + child.size();
            // 合并顶级职级单元格
            if (firstCol < lastCol) {
                sheet.addMergedRegion(new CellRangeAddress(1, 1, firstCol, lastCol));
            }

            // 填入顶级职级内容
            XSSFCell cellTopRankLevel = row1.createCell(indexCol + 1);
            cellTopRankLevel.setCellValue(topOaRankLevel.get(j).getRankName());
            cellTopRankLevel.setCellStyle(fontCellStyle);
            for (int i = 0; i < child.size(); i++) {
                // 填入子级内容
                XSSFCell childRankLevel = row2.createCell(indexCol + 1);
                childRankLevel.setCellValue(child.get(i).getRankName());
                childRankLevel.setCellStyle(fontCellStyle);
                // 固定职级对应列标
                rankIdToIndexCol.put(child.get(i).getId(), indexCol);

                indexCol++;
                // 设置列宽
                sheet.setColumnWidth(indexCol, sheet.getColumnWidth(indexCol) * 17 / 10);
            }

        }
        // 根据列数合并第一行标题单元格
        if (indexCol > 1) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, indexCol));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 绘制单元格内容：多少人即多少行
        for (int k = 0; k < nameList.size(); k++) {
            XSSFRow row = sheet.createRow(k + 3);
            XSSFCell name = row.createCell(0);
            name.setCellValue(nameList.get(k));

            int finalK = k;
            //根据固定列光标填充单元格内容
            rankIdToIndexCol.forEach((rankId, index) -> {
                // 在关系中找这个人这个等级，有则添加时间
                Optional<OaRankLevelExportDataListResp> first = relations.stream().filter(a -> a.getUserName().equals(nameList.get(finalK)) && a.getRankId().intValue() == rankId.intValue()).findFirst();
                if (first.isPresent()) {
                    // 创建当前行职级光标所在单元格
                    XSSFCell cell = row.createCell(index + 1);
                    // 填入评级时间到单元格
                    cell.setCellValue(format.format(first.get().getGradeTime()));
                }

            });
        }
        // 设置统计行
        XSSFRow total = sheet.createRow(nameList.size() + 4);
        XSSFCell totalCell = total.createCell(0);
        totalCell.setCellValue("当前等级人数");
        sheet.setColumnWidth(0, sheet.getColumnWidth(0) * 17 / 10);
        // 获取当前等级的数据（不包含历史）
        List<OaRankLevelUserRelation> relationList = oaRankLevelUserRelationService.list();
        rankIdToIndexCol.forEach((rankId, index) -> {
            // 在关系中找这个人这个等级的人有多少
            long count = relationList.stream().filter(a -> a.getRankId().intValue() == rankId.intValue()).count();
            XSSFCell cellTotal = total.createCell(index + 1);
            cellTotal.setCellValue(count);
            cellTotal.setCellStyle(fontCellStyle);
        });

        return workbook;
    }


    /**
     * 判断是否包含人员职级关系
     */
    public boolean checkExistRelation(Long id) {
        QueryWrapper<OaRankLevelUserRelation> queryWrapper = new QueryWrapper<>(OaRankLevelUserRelation.builder().rankId(id).build());
        long count = oaRankLevelUserRelationService.count(queryWrapper);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
}
