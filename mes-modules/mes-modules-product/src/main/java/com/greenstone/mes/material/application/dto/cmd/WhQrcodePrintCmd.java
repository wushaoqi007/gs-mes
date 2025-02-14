package com.greenstone.mes.material.application.dto.cmd;

import lombok.Data;

import java.util.List;

@Data
public class WhQrcodePrintCmd {

    private List<String> qrCodes;

    private Integer length;

    private Integer width;

}
