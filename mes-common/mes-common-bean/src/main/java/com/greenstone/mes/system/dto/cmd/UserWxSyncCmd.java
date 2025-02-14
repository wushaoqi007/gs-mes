package com.greenstone.mes.system.dto.cmd;

import lombok.Data;

import java.util.List;

@Data
public class UserWxSyncCmd {

    private List<Long> userIds;

}
