package com.greenstone.mes.office.api.factory;

import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.office.api.RemoteWxApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteWxApprovalFallbackFactory implements FallbackFactory<RemoteWxApprovalService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteWxApprovalFallbackFactory.class);

    @Override
    public RemoteWxApprovalService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteWxApprovalService() {
            @Override
            public R<String> commitCheckTakeApproval(WxApprovalCheckTakeCommitCmd command) {
                return R.fail("发送质检取件签名失败");
            }

            @Override
            public R<String> commitCheckedTakeApproval(WxApprovalCheckedTakeCommitCmd command) {
                return R.fail("发送合格品取件签名失败");
            }

            @Override
            public R<String> commitFinishedApproval(WxApprovalFinishedCommitCmd command) {
                return R.fail("发送出库签名失败");
            }
        };
    }
}
