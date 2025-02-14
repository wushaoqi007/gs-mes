package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteMachineFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteMachineService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteMachineFallbackFactory.class)
public interface RemoteMachineService {

    @PostMapping("/check/take/sign/finish")
    void checkTakeSignFinish(@RequestBody MachineSignFinishCmd signFinishCmd);

    @PostMapping("/checked/take/sign/finish")
    void checkedTakeSignFinish(@RequestBody MachineSignFinishCmd signFinishCmd);

    @PostMapping("/warehouse/out/sign/finish")
    void warehouseOutSignFinish(@RequestBody MachineSignFinishCmd signFinishCmd);
}
