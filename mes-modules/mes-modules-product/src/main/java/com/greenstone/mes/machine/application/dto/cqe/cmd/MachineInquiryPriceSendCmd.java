package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineInquiryPriceSendCmd {
    @NotEmpty(message = "请选择供应商")
    private List<String> providerList;
    @NotNull(message = "请填写截止日期")
    private String deadline;
    @NotNull(message = "请上传询价表格")
    private MultipartFile xlsxFile;
    @NotNull(message = "请上传附件")
    private List<MultipartFile> attachments;

    private List<MachineInquiryPriceSendVO> inquiryPriceParts;

}
