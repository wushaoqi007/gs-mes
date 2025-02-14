package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.result.IssueScanResult;
import com.greenstone.mes.material.domain.service.MaterialIssueService;
import com.greenstone.mes.material.infrastructure.mapper.MaterialIssueInvMapper;
import com.greenstone.mes.material.infrastructure.persistence.IssueInvPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MaterialIssueServiceImpl implements MaterialIssueService {

    private final MaterialIssueInvMapper issueInvMapper;


    @Override
    public List<IssueScanResult> listByMaterialCode(String materialCode) {
        return issueInvMapper.selectScanData(materialCode);
    }

    @Transactional
    @Override
    public void importMaterialIssue(Workbook wb, String fileName) {
        List<IssueInvPo> issueInvs = getIssueInvsFromExcel(wb, fileName);
        if (CollUtil.isEmpty(issueInvs)) {
            throw new RuntimeException("表格中缺少有效数据");
        }
        log.info("保存 {} {} 的组件清单, 共有记录 {} 条", issueInvs.get(0).getComponentCode(), issueInvs.get(0).getComponentName(), issueInvs.size());
        issueInvMapper.delete(Wrappers.lambdaQuery(IssueInvPo.class).eq(IssueInvPo::getProjectCode, issueInvs.get(0).getProjectCode()).eq(IssueInvPo::getComponentCode, issueInvs.get(0).getComponentCode()));
        issueInvMapper.insertBatchSomeColumn(issueInvs);
    }

    private List<IssueInvPo> getIssueInvsFromExcel(Workbook wb, String fileName) {
        List<IssueInvPo> invPos = new ArrayList<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(0);
            String projectCode = sheet.getRow(1).getCell(1).getStringCellValue();
            String deviceName = sheet.getRow(1).getCell(4).getStringCellValue();
            String componentCode = fileName.substring(0, 8);
            String componentName = sheet.getRow(1).getCell(6).getStringCellValue();
            String processCode = null, processName = null;
            String originMaterialName, materialCode, materialName, type;
            int number, suitNumber, totalNumber;

            int processCodeCol = 0, processNameCol = 1, originMaterialNameCol = 4, typeCol = 5;
            int numberCol = 6, suitNumberCol = 7, totalNumberCol = 8;

            // 从第四行开始，循环拿取行数据，直到零件名称所在的列是空为止
            StringBuilder errorInfo = new StringBuilder();


            int rowIndex = 3;
            try {
                while (sheet.getRow(rowIndex) != null && StrUtil.isNotBlank(sheet.getRow(rowIndex).getCell(originMaterialNameCol).getStringCellValue())) {
                    if (StrUtil.isNotBlank(sheet.getRow(rowIndex).getCell(processCodeCol).getStringCellValue())) {
                        processCode = sheet.getRow(rowIndex).getCell(processCodeCol).getStringCellValue();
                    }
                    if (StrUtil.isNotBlank(sheet.getRow(rowIndex).getCell(processNameCol).getStringCellValue())) {
                        processName = sheet.getRow(rowIndex).getCell(processNameCol).getStringCellValue();
                    }
                    type = sheet.getRow(rowIndex).getCell(typeCol).getStringCellValue().trim();
                    originMaterialName = sheet.getRow(rowIndex).getCell(originMaterialNameCol).getStringCellValue();
                    if ("机加工".equals(type)) {
                        if (!originMaterialName.contains(" ")) {
                            errorInfo.append(originMaterialName).append("</br>");
                        } else {
                            materialCode = originMaterialName.substring(0, originMaterialName.indexOf(" "));
                            materialName = originMaterialName.substring(originMaterialName.indexOf(" ") + 1);
                            number = (int) sheet.getRow(rowIndex).getCell(numberCol).getNumericCellValue();
                            suitNumber = (int) sheet.getRow(rowIndex).getCell(suitNumberCol).getNumericCellValue();
                            totalNumber = (int) sheet.getRow(rowIndex).getCell(totalNumberCol).getNumericCellValue();

                            invPos.add(IssueInvPo.builder().projectCode(projectCode).deviceName(deviceName).componentCode(componentCode).componentName(componentName)
                                    .processCode(processCode).processName(processName).originMaterialName(originMaterialName).materialCode(materialCode).materialName(materialName)
                                    .type(type).number(number).suitNumber(suitNumber).totalNumber(totalNumber).build());
                        }
                    }
                    rowIndex++;
                }
            } catch (Exception e) {
                String error = StrUtil.format("发生错误，第{}行，{}", rowIndex + 1, e.getMessage());
                throw new RuntimeException(error);
            }

            if (!errorInfo.isEmpty()) {
                throw new RuntimeException("以下零件名称不正确，请修改后重试</br>" + errorInfo);
            }
        }
        return invPos;
    }
}
