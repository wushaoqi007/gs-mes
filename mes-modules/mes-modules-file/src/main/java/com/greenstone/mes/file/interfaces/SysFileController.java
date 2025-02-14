package com.greenstone.mes.file.interfaces;

import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.file.application.service.FileService;
import com.greenstone.mes.file.infrastructure.persistence.FileRecord;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.api.domain.SysFileDomain;
import com.greenstone.mes.file.api.request.FileUploadReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class SysFileController {


    @Autowired
    private FileService fileService;


    @PostMapping("/download")
    public void download(HttpServletResponse response, @RequestBody Map<String, String> data) throws IOException {
        String path = data.get("path");
        InputStream file = fileService.getFile(path);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("文件".getBytes("gbk"), "UTF-8"));
        IOUtils.write(IOUtils.toByteArray(file), response.getOutputStream());
    }

    @GetMapping("/domain")
    public R<SysFileDomain> getDomain() {
        String fileDomain = fileService.getFileDomain();
        SysFileDomain domain = new SysFileDomain();
        domain.setDomain(fileDomain);
        return R.ok(domain);
    }

    @GetMapping("/info")
    public AjaxResult getFileInfoByPath(@RequestParam("path") String path) {
        return AjaxResult.success(fileService.getFileInfoByPath(path));
    }

    /**
     * 文件上传请求
     */
    @PostMapping("/upload")
    public R<SysFile> upload(MultipartFile file, Integer expireDay) {
        try {
            // 上传并返回访问地址
            SysFile url = fileService.uploadFile(file, expireDay);
            return R.ok(url);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 多文件上传请求
     */
    @PostMapping("/upload/multipart")
    public R<List<FileRecord>> uploadMultipart(FileUploadReq fileUploadReq) {
        try {
            // 上传并返回访问地址
            List<FileRecord> fileRecord = fileService.uploadFileMultipart(fileUploadReq);
            return R.ok(fileRecord);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/clear")
    public AjaxResult clear() {
        fileService.clearExpireFile();
        return AjaxResult.success();
    }

}