package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteMachineService;
import com.greenstone.mes.machine.dto.cmd.MachineSignFinishCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteMachineFallbackFactory implements FallbackFactory<RemoteMachineService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMachineFallbackFactory.class);

    @Override
    public RemoteMachineService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteMachineService() {
            @Override
            public void checkTakeSignFinish(MachineSignFinishCmd signFinishCmd) {

            }

            @Override
            public void checkedTakeSignFinish(MachineSignFinishCmd signFinishCmd) {

            }

            @Override
            public void warehouseOutSignFinish(MachineSignFinishCmd signFinishCmd) {

            }
        };
    }
}
