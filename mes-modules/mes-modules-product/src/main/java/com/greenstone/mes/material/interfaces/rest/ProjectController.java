package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.common.log.annotation.Log;
import com.greenstone.mes.common.log.enums.BusinessType;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.dto.ProjectImportCommand;
import com.greenstone.mes.material.dto.ProjectListQuery;
import com.greenstone.mes.material.interfaces.transfer.ProjectTransfer;
import com.greenstone.mes.material.application.service.ProjectManger;
import com.greenstone.mes.material.request.ProjectImportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 项目控制类
 *
 * @author wushaoqi
 * @date 2023-01-09-10:12
 */
@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {


    private final ProjectTransfer projectTransfer;

    private final ProjectManger projectManger;

    @Autowired
    public ProjectController(ProjectTransfer projectTransfer, ProjectManger projectManger) {
        this.projectTransfer = projectTransfer;
        this.projectManger = projectManger;
    }

    /**
     * 项目导入
     */
    @Log(title = "项目", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importPartOrder(MultipartFile file) {
        log.info("Receive project import request");
        // 将表格转为VO
        List<ProjectImportVO> importVOs = new ExcelUtil<>(ProjectImportVO.class).toList(file);
        log.info("Import content size: {}", importVOs.size());
        List<ProjectImportCommand.ProjectInfo> projectInfoImportCommands = projectTransfer.toImportCommands(importVOs);
        ProjectImportCommand projectImportCommand = ProjectImportCommand.builder().projects(projectInfoImportCommands).build();
        projectManger.importProject(projectImportCommand);
        return AjaxResult.success("导入成功");
    }

    @GetMapping("/list")
    public TableDataInfo list(ProjectListQuery query) {
        startPage();
        return getDataTable(projectManger.list(query));
    }
}
