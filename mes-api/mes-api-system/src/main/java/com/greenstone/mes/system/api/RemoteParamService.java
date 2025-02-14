package com.greenstone.mes.system.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.system.domain.BillParam;
import com.greenstone.mes.system.dto.cmd.UserParamSaveCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteParamService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteParamService {

    @PostMapping("/param/remembered")
    void saveUserParam(@RequestBody List<UserParamSaveCmd> saveCmdList);

    @PostMapping("/param/bill")
    void saveBillParam(@RequestBody List<UserParamSaveCmd> saveCmdList);

    @GetMapping("/param/bill")
    List<BillParam> list(@RequestParam("billType") String billType);

}
