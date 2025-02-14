package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class MachineOrderContractExportQuery {

    @NotEmpty(message = "单号不为空")
    private String serialNo;
    private String provider;
    private String purchaser;
    private LocalDate purchaseDate;
    private String contractNo;
    private String contactName;
    private String contactPhone;
    private String currency;
    private String shipTo;
    private String taxRate;
    private String phone;
    private String address;
    private String bank;
    private String account;
    private String taxNumber;


}
