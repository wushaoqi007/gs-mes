package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.assembler.WorksheetAssembler;
import com.greenstone.mes.material.cqe.command.WorksheetImportEditCommand;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.ProjectDO;
import com.greenstone.mes.material.domain.entity.Material;
import com.greenstone.mes.material.domain.entity.ProcessOrder;
import com.greenstone.mes.material.infrastructure.enums.ProcessOrderStatus;
import com.greenstone.mes.material.infrastructure.enums.ProcessPartStatus;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.domain.service.ProjectService;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.domain.service.WorksheetService;
import com.greenstone.mes.material.domain.types.MaterialId;
import com.greenstone.mes.material.domain.types.ProcessOrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/1 15:14
 */
@Slf4j
@Service
public class WorksheetRepository {

    private final WorksheetService worksheetService;
    private final WorksheetDetailService worksheetDetailService;
    private final WorksheetAssembler worksheetAssembler;
    private final IBaseMaterialService materialService;
    private final ProjectService projectService;
    private final RemoteSystemService remoteSystemService;

    @Autowired
    public WorksheetRepository(WorksheetService worksheetService, WorksheetAssembler worksheetAssembler,
                               WorksheetDetailService worksheetDetailService, IBaseMaterialService materialService,
                               ProjectService projectService, RemoteSystemService remoteSystemService) {
        this.worksheetService = worksheetService;
        this.worksheetAssembler = worksheetAssembler;
        this.worksheetDetailService = worksheetDetailService;
        this.materialService = materialService;
        this.projectService = projectService;
        this.remoteSystemService = remoteSystemService;
    }

    public synchronized void validateOrSavePart(ProcessOrder processOrder) {
        for (ProcessOrder.ProcessComponent component : processOrder.getComponents()) {
            for (ProcessOrder.ProcessPart part : component.getParts()) {
                Material material = part.getMaterial();
                BaseMaterial materialFind = materialService.queryBaseMaterial(BaseMaterial.builder().code(material.getCode()).version(material.getVersion()).build());
                if (Objects.nonNull(materialFind)) {
                    material.setId(new MaterialId(materialFind.getId()));
                    if (!materialFind.getName().equals(material.getName())) {
                        String errMsg = StrUtil.format("零件号'{}/{}'重复，当前的零件名称：'{}'，已存在的零件名称：'{}'",
                                material.getCode(), material.getVersion(), material.getName(), materialFind.getName());
                        throw new ServiceException(errMsg);
                    }
                } else {
                    BaseMaterial addReq = BaseMaterial.builder().code(part.getMaterial().getCode())
                            .version(part.getMaterial().getVersion())
                            .name(part.getMaterial().getName())
                            .rawMaterial(part.getMaterial().getRawMaterial())
                            .surfaceTreatment(part.getMaterial().getSurfaceTreatment())
                            .weight(part.getMaterial().getWeight())
                            .unit("pcs")
                            .type(1)
                            .designer(part.getMaterial().getDesigner()).build();
                    BaseMaterial baseMaterial = materialService.insertBaseMaterial(addReq, false);
                    material.setId(new MaterialId(baseMaterial.getId()));
                }
            }
        }
    }

    /**
     * 校验项目
     *
     * @param projectCode 项目代码
     */
    public void validateProject(String projectCode) {
        // 项目校验是否开启
        R<String> projectValidate = remoteSystemService.getConfigValueByKey("project.validate");
        if (projectValidate.isSuccess() && "true".equals(projectValidate.getMsg())) {
            ProjectDO projectDO = projectService.getOneOnly(ProjectDO.builder().projectCode(projectCode).build());
            if (Objects.isNull(projectDO)) {
                throw new ServiceException(BizError.E40002, projectCode);
            }
        }
    }

    public void saveProcessOrder(ProcessOrder processOrder) {
//        validateProject(processOrder.getProjectCode());
        validateOrSavePart(processOrder);
        ProcessOrderDO existDO = worksheetService.selectByCode(processOrder.getCode());
        if (Objects.isNull(existDO)) {
            ProcessOrderDO processOrderDO = worksheetAssembler.toProcessOrderDO(processOrder);
            processOrderDO.setStatus(String.valueOf(ProcessOrderStatus.TO_CONFIRM.getStatus()));
            worksheetService.save(processOrderDO);
            processOrder.setId(new ProcessOrderId(processOrderDO.getId()));
            insertProcessOrderDetails(processOrder);
        } else {
            if (Integer.parseInt(existDO.getStatus()) != ProcessOrderStatus.TO_CONFIRM.getStatus()) {
                throw new ServiceException(StrUtil.format("加工单'{}'已存在且已确认", processOrder.getCode()));
            }
            processOrder.setId(new ProcessOrderId(existDO.getId()));
            existDO.setPurchaseNumber((long) processOrder.getNumber());
            worksheetService.updateById(existDO);
            saveProcessOrderDetails(processOrder);
        }
    }

    private void insertProcessOrderDetails(ProcessOrder processOrder) {
        List<ProcessOrderDetailDO> insertList = new ArrayList<>();
        for (ProcessOrder.ProcessComponent component : processOrder.getComponents()) {
            for (ProcessOrder.ProcessPart part : component.getParts()) {
                ProcessOrderDetailDO processOrderDetailDO = worksheetAssembler.toProcessOrderDetailDO(processOrder, component, part);
                processOrderDetailDO.setStatus(String.valueOf(ProcessPartStatus.TO_CONFIRM.getStatus()));
                insertList.add(processOrderDetailDO);
            }
        }
        worksheetDetailService.saveBatch(insertList);
    }

    private void saveProcessOrderDetails(ProcessOrder processOrder) {
        List<ProcessOrderDetailDO> insertList = new ArrayList<>();
        List<ProcessOrderDetailDO> updateList = new ArrayList<>();
        for (ProcessOrder.ProcessComponent component : processOrder.getComponents()) {
            for (ProcessOrder.ProcessPart part : component.getParts()) {
                ProcessOrderDetailDO selectDO = ProcessOrderDetailDO.builder().processOrderId(processOrder.getId().id()).code(part.getMaterial().getCode()).version(part.getMaterial().getVersion()).build();
                ProcessOrderDetailDO existDO = worksheetDetailService.getOneOnly(selectDO);
                if (Objects.isNull(existDO)) {
                    ProcessOrderDetailDO insertDO = worksheetAssembler.toProcessOrderDetailDO(processOrder, component, part);
                    insertDO.setStatus(String.valueOf(ProcessPartStatus.TO_CONFIRM.getStatus()));
                    insertList.add(insertDO);
                } else {
                    existDO.setCurrentNumber((long) part.getNumber());
                    existDO.setOriginalNumber((long) part.getNumber());
                    updateList.add(existDO);
                }
            }
        }
        worksheetDetailService.saveBatch(insertList);
        worksheetDetailService.updateBatchById(updateList);
    }

    public void updateWorksheet(WorksheetImportEditCommand importEditCommand) {
        for (WorksheetImportEditCommand.PartImportEditCommand partImportEditCommand : importEditCommand.getPartImportEditCommands()) {
            ProcessOrderDO existDO = worksheetService.selectByCode(partImportEditCommand.getWorksheetCode());
            if (Objects.isNull(existDO)) {
                throw new ServiceException(BizError.E25001, partImportEditCommand.getWorksheetCode());
            }
            ProcessOrderDetailDO selectDO = ProcessOrderDetailDO.builder().processOrderId(existDO.getId())
                    .projectCode(partImportEditCommand.getProjectCode()).componentCode(partImportEditCommand.getComponentCode())
                    .code(partImportEditCommand.getPartCode()).version(partImportEditCommand.getPartVersion()).build();
            ProcessOrderDetailDO detailDO = worksheetDetailService.getOneOnly(selectDO);
            if (Objects.isNull(detailDO)) {
                throw new ServiceException(BizError.E25009, StrUtil.format("加工单：{}，项目代码：{}，组件号：{}，零件号/版本：{}/{}"
                        , partImportEditCommand.getWorksheetCode(), partImportEditCommand.getProjectCode(), partImportEditCommand.getComponentCode(), partImportEditCommand.getPartCode(), partImportEditCommand.getPartVersion()));
            }
            if (StrUtil.isNotEmpty(partImportEditCommand.getProvider())) {
                detailDO.setProvider(partImportEditCommand.getProvider());
            }
            if (Objects.nonNull(partImportEditCommand.getProcessingTime())) {
                detailDO.setProcessingTime(partImportEditCommand.getProcessingTime());
            }
            if (Objects.nonNull(partImportEditCommand.getPlanTime())) {
                detailDO.setPlanTime(partImportEditCommand.getPlanTime());
            }
            worksheetDetailService.updateById(detailDO);
        }

    }
}
