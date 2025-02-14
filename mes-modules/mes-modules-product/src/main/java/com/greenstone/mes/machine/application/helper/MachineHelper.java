package com.greenstone.mes.machine.application.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.Texts;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineOutStockCommand;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineStockChangeAddCmd;
import com.greenstone.mes.machine.application.dto.cqe.query.MachineOrderContractExportQuery;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartStockQuery;
import com.greenstone.mes.machine.application.dto.result.MachineMaterialUseResult;
import com.greenstone.mes.machine.application.dto.result.MachineOrderContractResult;
import com.greenstone.mes.machine.application.dto.result.MachinePartStockR;
import com.greenstone.mes.machine.domain.entity.*;
import com.greenstone.mes.machine.domain.repository.MachineProviderRepository;
import com.greenstone.mes.machine.domain.repository.MachineRequirementOldRepository;
import com.greenstone.mes.machine.domain.repository.MachineStockRepository;
import com.greenstone.mes.machine.infrastructure.constant.MachineParam;
import com.greenstone.mes.machine.infrastructure.enums.ReceiveType;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.IBaseWarehouseService;
import com.greenstone.mes.material.enums.WarehouseType;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class MachineHelper {

    private final IBaseMaterialService materialService;
    private final IBaseWarehouseService warehouseService;
    private final MachineStockRepository stockRepository;
    private final RemoteFileService fileService;
    private final MachineRequirementOldRepository requirementRepository;
    private final MachineProviderRepository providerRepository;


    /**
     * 仓库是否存在
     */
    public BaseWarehouse existWarehouseByCode(String warehouseCode) {
        BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code(warehouseCode).build());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException(MachineError.E200007, StrUtil.format("仓库编码：{}", warehouseCode));
        }
        return warehouse;
    }

    /**
     * 根据阶段找仓库
     */
    public BaseWarehouse findWarehouseByStage(WarehouseStage stage) {
        BaseWarehouse revokeWarehouse = warehouseService.findOnlyOneByStage(stage.getId());
        if (Objects.isNull(revokeWarehouse)) {
            throw new ServiceException(MachineError.E200007, StrUtil.format("仓库区域:{}", stage.getName()));
        }
        return revokeWarehouse;
    }

    /**
     * 查询收货时的出库仓库
     */
    public BaseWarehouse getReceiveOutWarehouse(Integer operation) {
        BaseWarehouse receiveOutWarehouse;
        switch (BillOperation.getById(operation)) {
            case RECEIVE_CREATE -> receiveOutWarehouse = findWarehouseByStage(WarehouseStage.WAIT_RECEIVE);
            case RECEIVE_TREAT_CREATE -> receiveOutWarehouse = findWarehouseByStage(WarehouseStage.TREATING);
            case RECEIVE_REWORKED_CREATE -> receiveOutWarehouse = findWarehouseByStage(WarehouseStage.REWORKING);
            default -> throw new ServiceException(MachineError.E200302, operation.toString());
        }
        return receiveOutWarehouse;
    }

    /**
     * 仓库是否存在，且不能是砧板
     */
    public BaseWarehouse existWarehouseNotBoard(String warehouseCode) {
        BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code(warehouseCode).build());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException(MachineError.E200007, StrUtil.format("仓库编码：{}", warehouseCode));
        }
        if (warehouse.getType() == WarehouseType.BOARD.getType()) {
            throw new ServiceException(MachineError.E200013);
        }
        return warehouse;
    }

    /**
     * 砧板是否可用
     *
     * @param warehouse 砧板
     * @param stage     使用阶段
     */
    public boolean usableBoard(BaseWarehouse warehouse, Integer stage) {
        if (!Objects.equals(warehouse.getStage(), stage)) {
            List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().warehouseId(warehouse.getId()).build());
            if (CollUtil.isNotEmpty(stockRList)) {
                throw new ServiceException(MachineError.E200012,
                        StrUtil.format("系统识别{}砧板存在其他区域零件，零件号/版本：{}/{}", warehouse.getCode(), stockRList.get(0).getPartCode(), stockRList.get(0).getPartVersion()));
            }
        }
        return true;
    }

    /**
     * 校验仓库是否可用
     */
    public boolean usableWarehouse(BaseWarehouse warehouse, WarehouseStage stage) {
        if (!Objects.equals(warehouse.getStage(), stage.getId())) {
            throw new ServiceException(MachineError.E200009, StrUtil.format("{}仓库不属于{}区域", warehouse.getCode(), stage.getName()));
        }
        return true;
    }

    /**
     * 获取可用仓库：如果是可用砧板，则修改砧板为可用阶段
     */
    public BaseWarehouse getUsableWarehouseByCode(String warehouseCode, Integer stage) {
        BaseWarehouse warehouse = warehouseService.queryWarehouseByCode(BaseWarehouse.builder().code(warehouseCode).build());
        if (Objects.isNull(warehouse)) {
            throw new ServiceException(MachineError.E200007, StrUtil.format("仓库编码：{}", warehouseCode));
        }
        // 可用的空砧板
        if (warehouse.getType() == WarehouseType.BOARD.getType() && usableBoard(warehouse, stage) && !Objects.equals(warehouse.getStage(), stage)) {
            warehouse.setStage(stage);
            warehouseService.updateBaseWarehouse(warehouse);
        }
        return warehouse;
    }

    public BaseMaterial checkMaterial(String partCode, String partVersion) {
        BaseMaterial baseMaterial = materialService.queryBaseMaterial(BaseMaterial.builder().code(partCode).version(partVersion).build());
        if (Objects.isNull(baseMaterial)) {
            throw new ServiceException(MachineError.E200005, StrUtil.format("物料编码/版本：{}/{}", partCode, partVersion));
        }
        return baseMaterial;
    }

    public BaseMaterial checkMaterialById(Long materialId) {
        BaseMaterial baseMaterial = materialService.selectBaseMaterialById(materialId);
        if (Objects.isNull(baseMaterial)) {
            throw new ServiceException(MachineError.E200005, StrUtil.format("物料id:{}", materialId));
        }
        return baseMaterial;
    }

    public MachineOutStockCommand.OutStockMaterial revokeStockCommandAssemble(MachineOrderDetail orderDetail, Long revokeNumber) {
        BaseMaterial baseMaterial = checkMaterialById(orderDetail.getMaterialId());
        return MachineOutStockCommand.OutStockMaterial.builder().material(baseMaterial)
                .orderSerialNo(orderDetail.getSerialNo()).number(revokeNumber).build();
    }

    public void revokeStock(List<MachineOutStockCommand.OutStockMaterial> outStockMaterialList) {
        // 库存撤回：直接删除库存
        if (CollUtil.isNotEmpty(outStockMaterialList)) {
            BaseWarehouse revokeWarehouse = findWarehouseByStage(WarehouseStage.WAIT_RECEIVE);
            MachineOutStockCommand outStockCmd = MachineOutStockCommand.builder().warehouse(revokeWarehouse).sponsor("system").forceOut(true).materialList(outStockMaterialList).build();
            log.info("库存还原：{}", outStockCmd);
            stockRepository.deleteStock(outStockCmd);
        }
    }

    public void revokeOrderStock(List<MachineOrderDetail> parts) {
        List<MachineOutStockCommand.OutStockMaterial> outStockMaterialList = new ArrayList<>();
        for (MachineOrderDetail orderDetail : parts) {
            outStockMaterialList.add(revokeStockCommandAssemble(orderDetail, orderDetail.getProcessNumber()));
        }
        // 撤回库存
        revokeStock(outStockMaterialList);
    }

    /**
     * 获取零件库存
     */
    public MachinePartStockR getPartStock(BaseWarehouse warehouse, String partCode, String partVersion) {
        BaseMaterial baseMaterial = materialService.queryBaseMaterial(BaseMaterial.builder().code(partCode).version(partVersion).build());
        if (baseMaterial == null) {
            throw new ServiceException(MachineError.E200005, StrUtil.format("物料编码/版本：{}/{}", partCode, partVersion));
        }
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().materialId(baseMaterial.getId()).warehouseCode(warehouse.getCode()).build());
        if (CollUtil.isEmpty(stockRList)) {
            return MachinePartStockR.builder().materialId(baseMaterial.getId()).partCode(baseMaterial.getCode()).partName(baseMaterial.getName())
                    .partVersion(baseMaterial.getVersion()).stockNumber(0L).designer(baseMaterial.getDesigner())
                    .warehouseId(warehouse.getId()).warehouseCode(warehouse.getCode()).warehouseName(warehouse.getName()).build();
        }
        return stockRList.get(0);
    }

    /**
     * 获取仓库所有库存
     */
    public List<MachinePartStockR> getStockAllByWarehouse(String warehouseCode) {
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().warehouseCode(warehouseCode).build());
        if (CollUtil.isEmpty(stockRList)) {
            throw new ServiceException(MachineError.E200010, StrUtil.format("仓库编码：{}", warehouseCode));
        }
        return stockRList;
    }

    /**
     * 获取物料所有库存
     */
    public List<MachinePartStockR> getStockAllByMaterial(Long materialId, String partCode, String partVersion) {
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().materialId(materialId).build());
        if (CollUtil.isEmpty(stockRList)) {
            throw new ServiceException(MachineError.E200010, StrUtil.format("零件号/版本：{}/{}", partCode, partVersion));
        }
        return stockRList;
    }

    /**
     * 获取库存数量
     */
    public Long getStockNumber(Long materialId, String warehouseCode) {
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().materialId(materialId).warehouseCode(warehouseCode).build());
        if (CollUtil.isNotEmpty(stockRList)) {
            return stockRList.get(0).getStockNumber();
        } else {
            return 0L;
        }
    }

    public Long getStockNumberWithProjectCode(String projectCode, Long materialId, String warehouseCode) {
        List<MachinePartStockR> stockRList = stockRepository.listStock(MachinePartStockQuery.builder().projectCode(projectCode).materialId(materialId).warehouseCode(warehouseCode).build());
        if (CollUtil.isNotEmpty(stockRList)) {
            return stockRList.get(0).getStockNumber();
        } else {
            return 0L;
        }
    }

    /**
     * 库存调整单创建
     *
     * @param stockChangeParts 库存调整零件
     */
    public MachineStockChangeAddCmd buildChangeStockCmd(List<MachineStockChangeAddCmd.Part> stockChangeParts) {
        MachineStockChangeAddCmd stockChangeAddCmd = MachineStockChangeAddCmd.builder().build();
        stockChangeAddCmd.setChangedById(SecurityUtils.getLoginUser().getUser().getUserId());
        stockChangeAddCmd.setChangedBy(SecurityUtils.getLoginUser().getUser().getNickName());
        stockChangeAddCmd.setChangedByNo(SecurityUtils.getLoginUser().getUser().getEmployeeNo());
        stockChangeAddCmd.setChangeTime(LocalDateTime.now());
        stockChangeAddCmd.setRemark("系统生成单据");
        stockChangeAddCmd.setParts(stockChangeParts);
        return stockChangeAddCmd;
    }

    /**
     * 机加工订单中是否存在某零件
     *
     * @return 找到的零件
     */
    public MachineOrderDetail existInMachineOrder(MachineOrder order, String requirementSerialNo, String projectCode, String partCode, String partVersion) {
        Optional<MachineOrderDetail> find = order.getParts().stream().filter(o -> o.getRequirementSerialNo().equals(requirementSerialNo)
                && o.getProjectCode().equals(projectCode)
                && o.getPartCode().equals(partCode)
                && o.getPartVersion().equals(partVersion)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException(StrUtil.format("零件不在订单中，订单号：{}，申请单号：{}，项目号：{}，零件号/版本：{}/{}", order.getSerialNo(), requirementSerialNo, projectCode, partCode, partVersion));
        }
        return find.get();
    }

    public MachineRequirementDetail existInMachineRequirement(MachineRequirement requirement, String projectCode, String partCode, String partVersion) {
        Optional<MachineRequirementDetail> find = requirement.getParts().stream().filter(o -> o.getProjectCode().equals(projectCode)
                && o.getPartCode().equals(partCode) && o.getPartVersion().equals(partVersion)).findFirst();
        if (find.isEmpty()) {
            throw new ServiceException(MachineError.E200122, StrUtil.format("申请单号：{}，项目号：{}，零件号/版本：{}/{}",
                    requirement.getSerialNo(), projectCode, partCode, partVersion));
        }
        return find.get();
    }

    public boolean allowTransfer(BaseWarehouse outWarehouse, BaseWarehouse inWarehouse) {
        if (inWarehouse.getType() == WarehouseType.WAREHOUSE.getType()) {
            return usableWarehouse(inWarehouse, WarehouseStage.getById(outWarehouse.getStage()));
        } else {
            return usableBoard(inWarehouse, outWarehouse.getStage());
        }
    }

    public SysFile materialUseGenWord(MachineMaterialUseResult useResult) {
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/lldmb.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(useResult.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (useResult.getParts().size() > 30) {
                page = Math.ceil((double) useResult.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<MachineMaterialUseDetail> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < useResult.getParts().size()) {
                        parts.add(useResult.getParts().get(i * 30 + j));
                    } else {
                        break;
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", useResult.getSerialNo());
                materialMap.put("useDate", useResult.getUseTime());
                materialMap.put("deptName", "测试部门");
                materialMap.put("warehouse", "DJ0001");
                materialMap.put("projectCode", "XM123121");
                materialMap.put("projectName", "项目0001");
                materialMap.put("startTime", "2024-05-06");
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                }
                // 底部文字（签名）
                String bottomWord = "\n制单人：" +
                        useResult.getSponsor() +
                        "            发料人：" +
                        useResult.getOperator() +
                        "            领料人：领料人";
                materialMap.put("bottomWord", Texts.of(bottomWord).create());
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(120, 120).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }

            // 1将文档写入到输出流
//            FileOutputStream outStream = new FileOutputStream("D:\\opt\\mes\\领料单.docx");
//            template.write(outStream);
            // 2将文档作为响应返回
//            response.setHeader("Content-Disposition", "attachment; filename=exported-word.docx");
//            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//            OutputStream outStream = new BufferedOutputStream(response.getOutputStream());
//            template.write(outStream);

            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", useResult.getSerialNo() + "领料单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SysFile orderContractGenExcel(MachineOrderContractResult contractResult, MachineOrderContractExportQuery query) {
        try {
            // 从resources下获取模板
            ClassPathResource classPathResource = new ClassPathResource("templates/htmb.xlsx");
            InputStream is = classPathResource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 基本信息
            Row row2 = sheet.getRow(2);
            Row row3 = sheet.getRow(3);
            Row row4 = sheet.getRow(4);
            Row row37 = sheet.getRow(37);
            StringBuilder signWord = new StringBuilder("供货单位（乙方）：");
            if (StrUtil.isNotEmpty(query.getProvider())) {
                String provider = row2.getCell(0).getStringCellValue();
                row2.getCell(0).setCellValue(provider + query.getProvider());
                signWord.append(query.getProvider());
            }
            if (StrUtil.isNotEmpty(query.getPurchaser())) {
                String purchaser = row2.getCell(4).getStringCellValue();
                row2.getCell(4).setCellValue(purchaser + query.getPurchaser());
            }
            if (StrUtil.isNotEmpty(query.getContractNo())) {
                String contractNo = row2.getCell(9).getStringCellValue();
                row2.getCell(9).setCellValue(contractNo + query.getContractNo());
            }
            if (StrUtil.isNotEmpty(query.getContactName())) {
                String contactName = row3.getCell(0).getStringCellValue();
                row3.getCell(0).setCellValue(contactName + query.getContactName());
            }
            if (query.getPurchaseDate() != null) {
                String purchaseDate = row3.getCell(4).getStringCellValue();
                row3.getCell(4).setCellValue(purchaseDate + LocalDateTimeUtil.format(query.getPurchaseDate(), "yyyy.MM.dd"));
            }
            if (StrUtil.isNotEmpty(query.getCurrency())) {
                row3.getCell(9).setCellValue("币种：" + query.getCurrency());
            }
            if (StrUtil.isNotEmpty(query.getContactPhone())) {
                String contactPhone = row4.getCell(0).getStringCellValue();
                row4.getCell(0).setCellValue(contactPhone + query.getContactPhone());
            }
            if (StrUtil.isNotEmpty(query.getShipTo())) {
                row4.getCell(4).setCellValue("送货地址：" + query.getShipTo());
            }
            if (StrUtil.isNotEmpty(query.getTaxRate())) {
                row4.getCell(9).setCellValue("税率：" + query.getTaxRate());
            }
            // 签字信息
            signWord.append("\n").append("电话：");
            if (StrUtil.isNotEmpty(query.getPhone())) {
                signWord.append(query.getPhone()).append("           ");
            } else {
                signWord.append("                   ");
            }
            signWord.append("地址：");
            if (StrUtil.isNotEmpty(query.getAddress())) {
                signWord.append(query.getAddress());
            }
            signWord.append("\n开户行：");
            if (StrUtil.isNotEmpty(query.getBank())) {
                signWord.append(query.getBank());
            }
            signWord.append("\n帐号：");
            if (StrUtil.isNotEmpty(query.getAccount())) {
                signWord.append(query.getAccount());
            }
            signWord.append("\n税号：");
            if (StrUtil.isNotEmpty(query.getTaxNumber())) {
                signWord.append(query.getTaxNumber());
            }
            signWord.append("\n法定代表人/委托代理人（签字）：");
            row37.getCell(5).setCellValue(signWord.toString());
            // 获取模板行格式
            Row tempRow = sheet.getRow(7);
            short height = tempRow.getHeight();
            CellStyle tempCellStyle = tempRow.getCell(1).getCellStyle();
            CellStyle timeCellStyle = tempRow.getCell(10).getCellStyle();
            // 金额小写格式
            Row totalPriceLowerCaseRow = sheet.getRow(8);
            CellStyle totalPriceLowerCaseStyle = totalPriceLowerCaseRow.getCell(9).getCellStyle();
            // 金额大写格式
            Row totalPriceUpperCaseRow = sheet.getRow(9);
            CellStyle totalPriceUpperCaseStyle = totalPriceUpperCaseRow.getCell(9).getCellStyle();
            double totalPrice = 0;
            // 向下移动n行，避免覆盖到
            if (contractResult.getParts().size() > 1) {
                sheet.shiftRows(8, sheet.getLastRowNum(), contractResult.getParts().size() - 1);
            }
            // 设置表格数据
            for (int i = 0; i < contractResult.getParts().size(); i++) {
                totalPrice += contractResult.getParts().get(i).getTotalPrice();
                Row row = sheet.createRow(i + 7);
                row.setHeight(height);
                row.createCell(0).setCellValue(i + 1);  //序号
                row.createCell(1).setCellValue(contractResult.getParts().get(i).getProjectCode().trim());
                row.createCell(2).setCellValue(contractResult.getParts().get(i).getHierarchy().trim());
                row.createCell(3).setCellValue(contractResult.getParts().get(i).getPartCode().trim() + "/" + contractResult.getParts().get(i).getPartVersion().trim());
                row.createCell(4).setCellValue(contractResult.getParts().get(i).getPartName().trim());
                row.createCell(5).setCellValue(contractResult.getParts().get(i).getProcessNumber());
                row.createCell(6).setCellValue(contractResult.getParts().get(i).getRawMaterial().trim());
                row.createCell(7).setCellValue(contractResult.getParts().get(i).getUnit());
                row.createCell(8).setCellValue(contractResult.getParts().get(i).getUnitPrice());
                row.createCell(9).setCellValue(contractResult.getParts().get(i).getTotalPrice());
                row.createCell(10).setCellValue(contractResult.getParts().get(i).getProcessDeadline());
                row.createCell(11).setCellValue(contractResult.getParts().get(i).getRemark());
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        // 将CellStyle应用到单元格上
                        if (j == 10) {
                            cell.setCellStyle(timeCellStyle);
                        } else {
                            cell.setCellStyle(tempCellStyle);
                        }
                    }
                }
            }
            XSSFRow totalLowerRow = sheet.getRow(7 + contractResult.getParts().size());
            XSSFCell totalLowerCell = totalLowerRow.getCell(9);
            totalLowerCell.setCellValue(totalPrice);
            totalLowerCell.setCellStyle(totalPriceLowerCaseStyle);
            XSSFRow totalUpperRow = sheet.getRow(8 + contractResult.getParts().size());
            XSSFCell totalUpperCell = totalUpperRow.getCell(9);
            totalUpperCell.setCellValue(totalPrice);
            totalUpperCell.setCellStyle(totalPriceUpperCaseStyle);

            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", contractResult.getSerialNo() + "合同" + System.currentTimeMillis() + ".xlsx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> inquiryPriceEmailCC() {
        String[] split = MachineParam.INQUIRY_PRICE_EMAIL_CC.split(";");
        return Arrays.asList(split);
    }

    public MachineRequirementDetail findRequirementPart(List<MachineRequirementDetail> machineRequirementDetails, String projectCode, String requirementSerialNo, String partCode, String partVersion) {
        if (StrUtil.isEmpty(requirementSerialNo)) {
            return null;
        }
        MachineRequirementDetail requirementDetail = null;
        Optional<MachineRequirementDetail> findRequirementPart = machineRequirementDetails.stream().filter(a -> a.getProjectCode().equals(projectCode)
                && a.getSerialNo().equals(requirementSerialNo)
                && a.getPartCode().equals(partCode) && a.getPartVersion().equals(partVersion)).findFirst();
        if (findRequirementPart.isPresent()) {
            requirementDetail = findRequirementPart.get();
        }
        return requirementDetail;
    }

    public Map<String, String> setHierarchyFromRequirement(Map<String, String> rowData, MachineRequirementDetail machineRequirementDetail) {
        if (machineRequirementDetail != null && StrUtil.isNotEmpty(machineRequirementDetail.getHierarchy())) {
            String[] split = splitByFirstTwoDigits(machineRequirementDetail.getHierarchy());
            rowData.put("hierarchy", split[0]);
            rowData.put("hierarchyName", split[1]);
        } else {
            rowData.put("hierarchy", "");
            rowData.put("hierarchyName", "");
        }
        return rowData;
    }

    public Map<String, String> getEmptyTemp() {
        Map<String, String> rowData = new HashMap<>();
        rowData.put("index", "");
        rowData.put("projectCode", "");
        rowData.put("hierarchy", "");
        rowData.put("hierarchyName", "");
        rowData.put("partCode", "");
        rowData.put("partVersion", "");
        rowData.put("number", "");
        rowData.put("type", "");
        return rowData;
    }

    public SysFile receiveGenWord(MachineReceive detail) {
        // 查询申请单数据
        List<MachineRequirementDetail> machineRequirementDetails = new ArrayList<>();
        Map<String, List<MachineReceiveDetail>> groupByRequirement = detail.getParts().stream().filter(a -> StrUtil.isNotEmpty(a.getRequirementSerialNo())).collect(Collectors.groupingBy(MachineReceiveDetail::getRequirementSerialNo));
        groupByRequirement.forEach((serialNo, list) -> machineRequirementDetails.addAll(requirementRepository.selectDetailList(serialNo)));
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/shdmb.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(detail.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (detail.getParts().size() > 30) {
                page = Math.ceil((double) detail.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<Map<String, String>> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < detail.getParts().size()) {
                        MachineReceiveDetail part = detail.getParts().get(i * 30 + j);
                        Map<String, String> rowData = new HashMap<>();
                        rowData.put("index", String.valueOf(i * 30 + j + 1));
                        rowData.put("projectCode", part.getProjectCode());
                        MachineRequirementDetail machineRequirementDetail = findRequirementPart(machineRequirementDetails, part.getProjectCode(), part.getRequirementSerialNo(), part.getPartCode(), part.getPartVersion());
                        setHierarchyFromRequirement(rowData, machineRequirementDetail);
                        rowData.put("partCode", part.getPartCode());
                        rowData.put("partVersion", part.getPartVersion());
                        rowData.put("number", String.valueOf(part.getActualNumber()));
                        rowData.put("type", ReceiveType.getByCode(part.getOperation()).getName());
                        parts.add(rowData);
                    } else {
                        // 可以加入空行？
                        parts.add(getEmptyTemp());
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", detail.getSerialNo());
                materialMap.put("receiveTime", LocalDateTimeUtil.format(detail.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
                materialMap.put("provider", detail.getProvider());
                materialMap.put("receiver", detail.getReceiver());
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                } else {
                    materialMap.put("isPageBreak", "");
                }
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(80, 80).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }
            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", detail.getSerialNo() + "收货单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SysFile checkTakeGenWord(MachineCheckTake detail) {
        // 查询申请单数据
        List<MachineRequirementDetail> machineRequirementDetails = new ArrayList<>();
        Map<String, List<MachineCheckTakeDetail>> groupByRequirement = detail.getParts().stream().filter(a -> StrUtil.isNotEmpty(a.getRequirementSerialNo())).collect(Collectors.groupingBy(MachineCheckTakeDetail::getRequirementSerialNo));
        groupByRequirement.forEach((serialNo, list) -> machineRequirementDetails.addAll(requirementRepository.selectDetailList(serialNo)));
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/zjqjdmb.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(detail.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (detail.getParts().size() > 30) {
                page = Math.ceil((double) detail.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<Map<String, String>> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < detail.getParts().size()) {
                        MachineCheckTakeDetail part = detail.getParts().get(i * 30 + j);
                        Map<String, String> rowData = new HashMap<>();
                        rowData.put("index", String.valueOf(i * 30 + j + 1));
                        rowData.put("projectCode", part.getProjectCode());
                        MachineRequirementDetail machineRequirementDetail = findRequirementPart(machineRequirementDetails, part.getProjectCode(), part.getRequirementSerialNo(), part.getPartCode(), part.getPartVersion());
                        setHierarchyFromRequirement(rowData, machineRequirementDetail);
                        rowData.put("partCode", part.getPartCode());
                        rowData.put("partVersion", part.getPartVersion());
                        rowData.put("number", String.valueOf(part.getTakeNumber()));
                        parts.add(rowData);
                    } else {
                        // 可以加入空行？
                        parts.add(getEmptyTemp());
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", detail.getSerialNo());
                materialMap.put("takeTime", LocalDateTimeUtil.format(detail.getTakeTime(), "yyyy-MM-dd HH:mm:ss"));
                materialMap.put("takeBy", detail.getTakeBy());
                materialMap.put("sponsor", detail.getSponsor());
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                } else {
                    materialMap.put("isPageBreak", "");
                }
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(80, 80).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }
            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", detail.getSerialNo() + "质检取件单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SysFile checkedTakeGenWord(MachineCheckedTake detail) {
        // 查询申请单数据
        List<MachineRequirementDetail> machineRequirementDetails = new ArrayList<>();
        Map<String, List<MachineCheckedTakeDetail>> groupByRequirement = detail.getParts().stream().filter(a -> StrUtil.isNotEmpty(a.getRequirementSerialNo())).collect(Collectors.groupingBy(MachineCheckedTakeDetail::getRequirementSerialNo));
        groupByRequirement.forEach((serialNo, list) -> machineRequirementDetails.addAll(requirementRepository.selectDetailList(serialNo)));
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/hgpqjdmb.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(detail.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (detail.getParts().size() > 30) {
                page = Math.ceil((double) detail.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<Map<String, String>> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < detail.getParts().size()) {
                        MachineCheckedTakeDetail part = detail.getParts().get(i * 30 + j);
                        Map<String, String> rowData = new HashMap<>();
                        rowData.put("index", String.valueOf(i * 30 + j + 1));
                        rowData.put("projectCode", part.getProjectCode());
                        MachineRequirementDetail machineRequirementDetail = findRequirementPart(machineRequirementDetails, part.getProjectCode(), part.getRequirementSerialNo(), part.getPartCode(), part.getPartVersion());
                        setHierarchyFromRequirement(rowData, machineRequirementDetail);
                        rowData.put("partCode", part.getPartCode());
                        rowData.put("partVersion", part.getPartVersion());
                        rowData.put("number", String.valueOf(part.getTakeNumber()));
                        parts.add(rowData);
                    } else {
                        // 可以加入空行？
                        parts.add(getEmptyTemp());
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", detail.getSerialNo());
                materialMap.put("takeTime", LocalDateTimeUtil.format(detail.getTakeTime(), "yyyy-MM-dd HH:mm:ss"));
                materialMap.put("takeBy", detail.getTakeBy());
                materialMap.put("sponsor", detail.getSponsor());
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                } else {
                    materialMap.put("isPageBreak", "");
                }
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(80, 80).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }
            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", detail.getSerialNo() + "合格品取件单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SysFile checkGenWord(MachineCheck detail) {
        // 查询申请单数据
        List<MachineRequirementDetail> machineRequirementDetails = new ArrayList<>();
        Map<String, List<MachineCheckDetail>> groupByRequirement = detail.getParts().stream().filter(a -> StrUtil.isNotEmpty(a.getRequirementSerialNo())).collect(Collectors.groupingBy(MachineCheckDetail::getRequirementSerialNo));
        groupByRequirement.forEach((serialNo, list) -> machineRequirementDetails.addAll(requirementRepository.selectDetailList(serialNo)));
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/jyd.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(detail.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (detail.getParts().size() > 30) {
                page = Math.ceil((double) detail.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<Map<String, String>> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < detail.getParts().size()) {
                        MachineCheckDetail part = detail.getParts().get(i * 30 + j);
                        Map<String, String> rowData = new HashMap<>();
                        rowData.put("index", String.valueOf(i * 30 + j + 1));
                        rowData.put("projectCode", part.getProjectCode());
                        MachineRequirementDetail machineRequirementDetail = findRequirementPart(machineRequirementDetails, part.getProjectCode(), part.getRequirementSerialNo(), part.getPartCode(), part.getPartVersion());
                        setHierarchyFromRequirement(rowData, machineRequirementDetail);
                        rowData.put("partCode", part.getPartCode());
                        rowData.put("partVersion", part.getPartVersion());
                        rowData.put("number", String.valueOf(part.getCheckedNumber()));
                        parts.add(rowData);
                    } else {
                        // 可以加入空行,空行也要有模板字段，否则会渲染报错很多日志输出
                        parts.add(getEmptyTemp());
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", detail.getSerialNo());
                materialMap.put("checkDate", LocalDateTimeUtil.format(detail.getCheckTime(), "yyyy-MM-dd"));
                materialMap.put("checkBy", detail.getCheckBy());
                materialMap.put("type", detail.getCheckResultType().getName());
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                } else {
                    materialMap.put("isPageBreak", "");
                }
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(80, 80).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }
            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", detail.getSerialNo() + "检验单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SysFile warehouseOutGenWord(MachineWarehouseOut detail) {
        // 查询申请单数据
        List<MachineRequirementDetail> machineRequirementDetails = new ArrayList<>();
        Map<String, List<MachineWarehouseOutDetail>> groupByRequirement = detail.getParts().stream().filter(a -> StrUtil.isNotEmpty(a.getRequirementSerialNo())).collect(Collectors.groupingBy(MachineWarehouseOutDetail::getRequirementSerialNo));
        groupByRequirement.forEach((serialNo, list) -> machineRequirementDetails.addAll(requirementRepository.selectDetailList(serialNo)));
        try {
            // 从resources下获取模板
            ClassPathResource resource = new ClassPathResource("templates/ckdmb.docx");
            InputStream inputStream = resource.getInputStream();
            // 添加二维码图片
            BufferedImage qrCodeImage = QrCodeUtil.generate(detail.getSerialNo(), QrConfig.create().setMargin(1));
            // 计算页数：每页30行，新页都要有表头
            double page = 1;
            if (detail.getParts().size() > 30) {
                page = Math.ceil((double) detail.getParts().size() / 30);
            }
            List<Map<String, Object>> foreachList = new ArrayList<>();
            // 区块标签
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("list", foreachList);
            for (int i = 0; i < page; i++) {
                log.info("第{}页表格数据", i + 1);
                List<Map<String, String>> parts = new ArrayList<>();
                for (int j = 0; j < 30; j++) {
                    if (i * 30 + j < detail.getParts().size()) {
                        MachineWarehouseOutDetail part = detail.getParts().get(i * 30 + j);
                        Map<String, String> rowData = new HashMap<>();
                        rowData.put("index", String.valueOf(i * 30 + j + 1));
                        rowData.put("projectCode", part.getProjectCode());
                        MachineRequirementDetail machineRequirementDetail = findRequirementPart(machineRequirementDetails, part.getProjectCode(), part.getRequirementSerialNo(), part.getPartCode(), part.getPartVersion());
                        setHierarchyFromRequirement(rowData, machineRequirementDetail);
                        rowData.put("partCode", part.getPartCode());
                        rowData.put("partVersion", part.getPartVersion());
                        rowData.put("number", String.valueOf(part.getOutStockNumber()));
                        parts.add(rowData);
                    } else {
                        // 可以加入空行？
                        parts.add(getEmptyTemp());
                    }
                }
                Map<String, Object> materialMap = new HashMap<>();
                // 表格行数据循环标签：置于循环行的上一行
                materialMap.put("tables", parts);
                // 其他标签
                materialMap.put("serialNo", detail.getSerialNo());
                materialMap.put("takeTime", LocalDateTimeUtil.format(detail.getOutStockTime(), "yyyy-MM-dd HH:mm:ss"));
                materialMap.put("takeBy", detail.getApplicant());
                materialMap.put("sponsor", detail.getSponsor());
                // 插入分页标记，最后一页不用分页
                if (i != page - 1) {
                    materialMap.put("isPageBreak", "分页标记");
                } else {
                    materialMap.put("isPageBreak", "");
                }
                materialMap.put("qrCode", Pictures.ofBufferedImage(qrCodeImage, PictureType.PNG).size(80, 80).create());
                foreachList.add(materialMap);
            }

            //渲染表格：将插件应用到表格标签
            LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
            Configure config = Configure.builder().bind("tables", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(inputStream, config).render(resMap);
            // 替换分页标记为分页符
            List<XWPFParagraph> paragraphs = template.getXWPFDocument().getParagraphs();
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("分页标记")) {
                            text = text.replace("分页标记", "");
                            r.setText(text, 0);
                            r.addBreak(BreakType.PAGE);
                        }
                    }
                }
            }
            // 将word转为输出流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            template.write(outStream);
            template.close();
            // 3将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", detail.getSerialNo() + "出库单" + System.currentTimeMillis() + ".docx", null, outStream.toByteArray());
            outStream.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            if (StringUtils.isNull(upload) || StringUtils.isNull(upload.getData())) {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            return upload.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据前两位数字分割字符串
     */
    public static String[] splitByFirstTwoDigits(String input) {
        if (StrUtil.isEmpty(input)) {
            return new String[]{"", ""};
        }
        if (!input.matches("^\\d{2}.*")) {
            return new String[]{"01", input};
        }
        // 截取前两位数字
        String firstTwoDigits = input.substring(0, 2);
        // 截取剩余部分
        String remainingPart = input.substring(2);

        // 返回分割后的结果
        return new String[]{firstTwoDigits, remainingPart};
    }

    public List<MailAddress> defaultEmailRecipientOfRequirementApproval() {
        List<MailAddress> mailAddresses = new ArrayList<>();
        if (StrUtil.isNotEmpty(MachineParam.MACHINE_REQUIREMENT_EMAIL_RECIPIENT)) {
            String[] split = MachineParam.MACHINE_REQUIREMENT_EMAIL_RECIPIENT.split(";");
            for (String email : split) {
                mailAddresses.add(assembleMailAddress(email));
            }
        }
        return mailAddresses;
    }

    public MailAddress assembleMailAddress(String email) {
        String personal = null;
        if (email.contains("-")) {
            String[] emailWithName = email.split("-");
            email = emailWithName[0];
            personal = emailWithName[1];
        }
        return new MailAddress(email, personal);
    }

    public List<MailAddress> defaultEmailCCOfRequirementApproval() {
        List<MailAddress> mailAddresses = new ArrayList<>();
        if (StrUtil.isNotEmpty(MachineParam.MACHINE_REQUIREMENT_EMAIL_CC)) {
            String[] split = MachineParam.MACHINE_REQUIREMENT_EMAIL_CC.split(";");
            for (String email : split) {
                mailAddresses.add(assembleMailAddress(email));
            }
        }
        return mailAddresses;
    }

    public void trim(MachineRequirement requirement) {
        List<String> trims = List.of(" ", "-", "_", "/");
        for (MachineRequirementDetail part : requirement.getParts()) {
            if (StrUtil.isNotBlank(part.getPartCode())) {
                String partCode;
                partCode = StrUtil.trim(part.getPartCode(), 0, character -> trims.contains(String.valueOf(character)));
                partCode = partCode.replaceAll("\r", "");
                partCode = partCode.replaceAll("\n", "");
                part.setPartCode(partCode);
            }
            if (StrUtil.isNotBlank(part.getPartName())) {
                String partName;
                partName = StrUtil.trim(part.getPartName(), 0, character -> trims.contains(String.valueOf(character)));
                partName = partName.replaceAll("\r", "");
                partName = partName.replaceAll("\n", "");
                part.setPartName(partName);
            }
            if (StrUtil.isNotEmpty(part.getRawMaterial())) {
                String rawMaterial = StrUtil.trim(part.getRawMaterial(), 0, character -> trims.contains(String.valueOf(character)));
                rawMaterial = rawMaterial.replaceAll("\r", "");
                rawMaterial = rawMaterial.replaceAll("\n", "");
                part.setRawMaterial(rawMaterial);
            }
            if (StrUtil.isNotEmpty(part.getSurfaceTreatment())) {
                String surfaceTreatment = StrUtil.trim(part.getSurfaceTreatment(), 0, character -> trims.contains(String.valueOf(character)));
                surfaceTreatment = surfaceTreatment.replaceAll("\r", "");
                surfaceTreatment = surfaceTreatment.replaceAll("\n", "");
                part.setSurfaceTreatment(surfaceTreatment);
            }
            if (StrUtil.isNotEmpty(part.getHierarchy())) {
                String hierarchy = StrUtil.trim(part.getHierarchy(), 0, character -> trims.contains(String.valueOf(character)));
                hierarchy = hierarchy.replaceAll("\r", "");
                hierarchy = hierarchy.replaceAll("\n", "");
                part.setHierarchy(hierarchy);
            }
        }
    }

    public void trim(MachineOrder order) {
        List<String> trims = List.of(" ", "-", "_", "/");
        for (MachineOrderDetail part : order.getParts()) {
            if (StrUtil.isNotBlank(part.getPartCode())) {
                String partCode;
                partCode = StrUtil.trim(part.getPartCode(), 0, character -> trims.contains(String.valueOf(character)));
                partCode = partCode.replaceAll("\r", "");
                partCode = partCode.replaceAll("\n", "");
                part.setPartCode(partCode);
            }
            if (StrUtil.isNotBlank(part.getPartName())) {
                String partName;
                partName = StrUtil.trim(part.getPartName(), 0, character -> trims.contains(String.valueOf(character)));
                partName = partName.replaceAll("\r", "");
                partName = partName.replaceAll("\n", "");
                part.setPartName(partName);
            }
            if (StrUtil.isNotBlank(part.getPartVersion())) {
                String partVersion;
                partVersion = StrUtil.trim(part.getPartVersion(), 0, character -> trims.contains(String.valueOf(character)));
                partVersion = partVersion.replaceAll("\r", "");
                partVersion = partVersion.replaceAll("\n", "");
                part.setPartVersion(partVersion);
            }
            if (StrUtil.isNotEmpty(part.getRawMaterial())) {
                String rawMaterial = StrUtil.trim(part.getRawMaterial(), 0, character -> trims.contains(String.valueOf(character)));
                rawMaterial = rawMaterial.replaceAll("\r", "");
                rawMaterial = rawMaterial.replaceAll("\n", "");
                part.setRawMaterial(rawMaterial);
            }
            if (StrUtil.isNotEmpty(part.getSurfaceTreatment())) {
                String surfaceTreatment = StrUtil.trim(part.getSurfaceTreatment(), 0, character -> trims.contains(String.valueOf(character)));
                surfaceTreatment = surfaceTreatment.replaceAll("\r", "");
                surfaceTreatment = surfaceTreatment.replaceAll("\n", "");
                part.setSurfaceTreatment(surfaceTreatment);
            }
            if (StrUtil.isNotEmpty(part.getHierarchy())) {
                String hierarchy = StrUtil.trim(part.getHierarchy(), 0, character -> trims.contains(String.valueOf(character)));
                hierarchy = hierarchy.replaceAll("\r", "");
                hierarchy = hierarchy.replaceAll("\n", "");
                part.setHierarchy(hierarchy);
            }
        }
    }

    public void trim(MachineInquiryPrice inquiryPrice) {
        List<String> trims = List.of(" ", "-", "_", "/");
        for (MachineInquiryPriceDetail part : inquiryPrice.getParts()) {
            if (StrUtil.isNotBlank(part.getPartCode())) {
                String partCode;
                partCode = StrUtil.trim(part.getPartCode(), 0, character -> trims.contains(String.valueOf(character)));
                partCode = partCode.replaceAll("\r", "");
                partCode = partCode.replaceAll("\n", "");
                part.setPartCode(partCode);
            }
            if (StrUtil.isNotBlank(part.getPartName())) {
                String partName;
                partName = StrUtil.trim(part.getPartName(), 0, character -> trims.contains(String.valueOf(character)));
                partName = partName.replaceAll("\r", "");
                partName = partName.replaceAll("\n", "");
                part.setPartName(partName);
            }
            if (StrUtil.isNotBlank(part.getPartVersion())) {
                String partVersion;
                partVersion = StrUtil.trim(part.getPartVersion(), 0, character -> trims.contains(String.valueOf(character)));
                partVersion = partVersion.replaceAll("\r", "");
                partVersion = partVersion.replaceAll("\n", "");
                part.setPartVersion(partVersion);
            }
        }
    }

    public void requirementGeneralValidate(MachineRequirement requirement) {
        trim(requirement);
        // 加工单号的规则：客户代码+7位数字+8位日期+3位字母+2位序号
        if (!requirement.getSerialNo().matches("[A-Z]+[0-9]{15}[A-Z]{3}[0-9]+") && !requirement.getSerialNo().matches("[A-Z]+[0-9]{4}[A-Z]{2}[0-9]{3}[0-9]{8}[A-Z]{3}[0-9]+")) {
            throw new ServiceException(MachineError.E200002, StrUtil.format("客户代码+（7位数字）或（4位数字+2位字母+3位数字）+8位日期+3位字母+2位序号。", requirement.getSerialNo()));
        }
        if (StrUtil.isBlank(requirement.getProjectCode())) {
            throw new ServiceException("项目代码不能为空");
        }
        if (CollUtil.isEmpty(requirement.getParts())) {
            throw new ServiceException("零件列表不能为空");
        }
        for (MachineRequirementDetail part : requirement.getParts()) {
            if (StrUtil.isBlank(part.getPartName())) {
                throw new ServiceException("零件名称不能为空");
            }
            if (StrUtil.isBlank(part.getPartCode())) {
                throw new ServiceException("零件号不能为空");
            }
            if (StrUtil.isBlank(part.getPartVersion())) {
                throw new ServiceException("零件版本不能为空");
            }
            // 版本必须是大写的V加一个数字
            if (part.getPartVersion().charAt(0) != 'V') {
                throw new ServiceException("零件版本必须是大写的V");
            }
            if (part.getPartVersion().length() != 2 || !CharUtil.isNumber(part.getPartVersion().charAt(1))) {
                throw new ServiceException("版本必须是大写的V加一个数字");
            }
            if (part.getProcessNumber() == null) {
                throw new ServiceException("零件数量不能为空");
            }
            if (part.getPaperNumber() == null) {
                throw new ServiceException("图纸数量不能为空");
            }
            // 零件号格式为4个字母+4个数字
            char[] chars = part.getPartCode().toCharArray();
            int count1 = 0;
            int count2 = 0;
            for (char c : chars) {
                if (Character.isLetter(c)) {
                    count1++;
                } else if (Character.isDigit(c)) {
                    count2++;
                }
            }
            if (count1 != 4 || count2 != 4) {
                throw new ServiceException(StrUtil.format("零件号应由4个字母+4个数字组成：{}", part.getPartCode()));
            }
        }
        // 同一个表格不能添加重复零件
        Map<String, List<MachineRequirementDetail>> groupByCodeAndVersion = requirement.getParts().stream().collect(Collectors.groupingBy(p -> p.getPartCode() + "/" + p.getPartVersion()));
        groupByCodeAndVersion.forEach((partAndVersion, list) -> {
            if (list.size() > 1) {
                throw new ServiceException(StrUtil.format("零件重复，零件号/版本：{}", partAndVersion));
            }
        });
    }

    public void requirementPageValidate(MachineRequirement requirement) {
        if (requirement.getReceiveDeadline() == null) {
            throw new ServiceException("期望到货时间不能为空");
        }
        if (StrUtil.isBlank(requirement.getTitle())) {
            throw new ServiceException("标题不能为空");
        }
        if (CollUtil.isEmpty(requirement.getApprovers())) {
            throw new ServiceException("审批人不能为空");
        }
        if (CollUtil.isEmpty(requirement.getCopyTo())) {
            throw new ServiceException("抄送人不能为空");
        }
    }

    public void orderPageValidate(MachineOrder order) {
        if (StrUtil.isBlank(order.getProvider())) {
            throw new ServiceException("供应商不能为空");
        }
        // 校验加工商
        providerRepository.findByName(order.getProvider());
        if (order.getOrderTime() == null) {
            throw new ServiceException("下单日期不能为空");
        }
        if (CollUtil.isEmpty(order.getParts())) {
            throw new ServiceException("零件列表不能为空");
        }
    }

    public void orderGeneralValidate(MachineOrder order) {
        trim(order);
        for (MachineOrderDetail part : order.getParts()) {
            if (StrUtil.isBlank(part.getProjectCode())) {
                throw new ServiceException("项目代码不能为空");
            }
            if (StrUtil.isBlank(part.getPartName())) {
                throw new ServiceException("零件名称不能为空");
            }
            if (StrUtil.isBlank(part.getPartCode())) {
                throw new ServiceException("零件号不能为空");
            }
            if (StrUtil.isBlank(part.getPartVersion())) {
                throw new ServiceException("零件版本不能为空");
            }
            // 版本必须是大写的V加一个数字
            if (part.getPartVersion().charAt(0) != 'V') {
                throw new ServiceException("零件版本必须是大写的V");
            }
            if (part.getPartVersion().length() != 2 || !CharUtil.isNumber(part.getPartVersion().charAt(1))) {
                throw new ServiceException("版本必须是大写的V加一个数字");
            }
            if (part.getProcessNumber() == null) {
                throw new ServiceException("购买数量不能为空");
            }

        }
        // 同一个表格不能添加重复零件
        Map<String, List<MachineOrderDetail>> groupByCodeAndVersion = order.getParts().stream().collect(Collectors.groupingBy(p -> p.getRequirementSerialNo() + ":" + p.getPartCode() + "/" + p.getPartVersion()));
        groupByCodeAndVersion.forEach((partAndVersion, list) -> {
            if (list.size() > 1) {
                throw new ServiceException(StrUtil.format("零件重复，申请单号：零件号/版本【{}】", partAndVersion));
            }
        });
    }

    public void inquiryPriceGeneralValidate(MachineInquiryPrice inquiryPrice) {
        trim(inquiryPrice);
        for (MachineInquiryPriceDetail part : inquiryPrice.getParts()) {
            if (StrUtil.isBlank(part.getRequirementSerialNo())) {
                throw new ServiceException("申请单号不能为空");
            }
            if (part.getRequirementDetailId() == null) {
                throw new ServiceException("申请单详情id不能为空");
            }
            if (StrUtil.isBlank(part.getProjectCode())) {
                throw new ServiceException("项目代码不能为空");
            }
            if (part.getMaterialId() == null) {
                throw new ServiceException("物料id不能为空");
            }
            if (StrUtil.isBlank(part.getPartName())) {
                throw new ServiceException("零件名称不能为空");
            }
            if (StrUtil.isBlank(part.getPartCode())) {
                throw new ServiceException("零件号不能为空");
            }
            if (StrUtil.isBlank(part.getPartVersion())) {
                throw new ServiceException("零件版本不能为空");
            }
            // 版本必须是大写的V加一个数字
            if (part.getPartVersion().charAt(0) != 'V') {
                throw new ServiceException("零件版本必须是大写的V");
            }
            if (part.getPartVersion().length() != 2 || !CharUtil.isNumber(part.getPartVersion().charAt(1))) {
                throw new ServiceException("版本必须是大写的V加一个数字");
            }
            if (part.getPartNumber() == null) {
                throw new ServiceException("零件数量不能为空");
            }
            if (part.getPaperNumber() == null) {
                throw new ServiceException("图纸数量不能为空");
            }
            if (part.getScannedPaperNumber() == null) {
                throw new ServiceException("已扫描图纸数量不能为空");
            }
        }

    }

    /**
     * object 转 list
     *
     * @param obj 需要转换的List对象
     */
    public static <T> List<T> ObjectToList(Object obj) {
        // 判断 obj 是否包含 List 类型
        if (obj instanceof List<?>) {
            return new ArrayList<>((List<T>) obj);
        }
        return null;
    }
}
