package com.greenstone.mes.file.api.factory;

import com.greenstone.mes.file.api.request.FileUploadReq;
import com.greenstone.mes.system.api.domain.FileRecord;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;

import java.util.List;
import java.util.Map;

/**
 * 文件服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable) {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService() {
            @Override
            public R<SysFile> upload(MultipartFile file) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public R<SysFile> upload(MultipartFile file, Integer expireDay) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public R<List<FileRecord>> info(Long relationId, Integer relationType) {
                return R.fail("获取文件失败:" + throwable.getMessage());
            }

            @Override
            public R<List<FileRecord>> uploadMultipart(FileUploadReq fileUploadReq) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public Response download(Map<String, String> data) {
                return null;
            }

            @Override
            public FileRecord getFileInfoByPath(String path) {
                return null;
            }

        };
    }
}
