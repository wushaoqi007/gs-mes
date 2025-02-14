package com.greenstone.mes.machine.application.service.impl;

import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineReceiveAddCmd;
import com.greenstone.mes.machine.application.service.MachineReceiveService;
import com.greenstone.mes.material.domain.service.IBaseMaterialService;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MachineReceiveServiceImplTest {

    @Autowired
    private MachineReceiveService machineReceiveService;

    @Autowired
    private IBaseMaterialService baseMaterialService;

    @Test
    public void saveCommit() {
        MachineReceiveAddCmd addCmd = new MachineReceiveAddCmd();
        addCmd.setReceiver("顾仁凯");
        addCmd.setReceiverId(1732L);
        addCmd.setReceiverNo("G00178");
        addCmd.setProvider("德力机械");
        addCmd.setReceiveTime(LocalDateTime.now());

        List<MachineReceiveAddCmd.Part> parts = new ArrayList<>();
        addCmd.setParts(parts);

        MachineReceiveAddCmd.Part part1 = MachineReceiveAddCmd.Part.builder().partCode("GRKA0001").partVersion("V0").actualNumber(2L).projectCode("GRK2400001").operation(BillOperation.RECEIVE_CREATE.getId()).materialId("164369").warehouseCode("01-01-01").orderSerialNo("GLSTZDH2024120609DLJX").requirementSerialNo("GRK240000120241206GRK01").build();
        parts.add(part1);

        machineReceiveService.saveCommit(addCmd);
    }
}