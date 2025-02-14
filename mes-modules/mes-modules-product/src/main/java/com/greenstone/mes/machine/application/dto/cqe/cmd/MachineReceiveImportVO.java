package com.greenstone.mes.machine.application.dto.cqe.cmd;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 收货单导入
 */
@Data
public class MachineReceiveImportVO {

    @Excel(name = "加工单位")
    @NotEmpty(message = "加工单位不能为空")
    private String provider;

    @Excel(name = "收货日期", dateFormat = "yyyy/MM/dd")
    @NotNull(message = "收货日期不能为空")
    private Date receiveTime;

    @Excel(name = "生产代码")
    @NotEmpty(message = "生产代码不能为空")
    private String projectCode;
    /**
     * code+空格+name+version，版本可以为空
     */
    @Excel(name = "零件名称")
    @NotEmpty(message = "零件名称不能为空")
    private String partCodeNameVersion;

    @Excel(name = "订单数量")
    @NotNull(message = "订单数量不能为空")
    private Integer orderNumber;

    @Excel(name = "收货数量")
    @NotNull(message = "收货数量不能为空")
    private Integer receivedNumber;

    @Excel(name = "备注")
    private String remark;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartCodeNameVersion {
        private String code;

        private String name;

        private String version;
    }


    public PartCodeNameVersion validAndGetPartCodeNameVersion() {
        String code;
        String name = null;
        String version = null;
        // codeNameVersion 如：IAYC7193 固定结构件V0
        // 拆分零件编码、名称、版本，编码、名称版本中间使用空格分割，如果有版本则名称和版本连在一起
        String[] codeNameVersion = partCodeNameVersion.split(" ");
        if (codeNameVersion.length < 2) {
            throw new ServiceException(StrUtil.format("零件名称不正确: {}", partCodeNameVersion));
        }
        code = codeNameVersion[0];
        // 只允许编码8位的规范格式，排斥【404-034 V1滚轮安装板2】、【606-018 加强筋2】 等
        if (code.length() != 8) {
            throw new ServiceException(StrUtil.format("零件编码不正确：{}", code));
        }
        // codeNameVersion 的倒数第二个字符为V或v时，表示最后两位是版本
        String nameVersion = partCodeNameVersion.substring(code.length() + 1);
        if (StrUtil.isEmpty(nameVersion)) {
            throw new ServiceException(StrUtil.format("零件名称版本不正确：{}", nameVersion));
        }
        if (nameVersion.length() > 2) {
            char secondLastChar = nameVersion.charAt(nameVersion.length() - 2);
            if (secondLastChar == 'V' || secondLastChar == 'v') {
                name = nameVersion.substring(0, nameVersion.length() - 2);
                version = nameVersion.substring(nameVersion.length() - 2).toUpperCase();
            }
        }
        if (name == null) {
            name = nameVersion;
        }
        if (version == null) {
            version = "V0";
        }
        return PartCodeNameVersion.builder().code(code).version(version).name(name).build();
    }

}
