package com.greenstone.mes.material.infrastructure.util;

import com.greenstone.mes.material.application.dto.OutStockCommand;

/**
 * @author gu_renkai
 * @date 2023/1/16 16:56
 */

public class StockTransferUtil {

    private static final ThreadLocal<OutStockCommand> commandThreadLocal = new ThreadLocal<>();

    public static void setOutStockCommand(OutStockCommand outStockCommand) {
        commandThreadLocal.set(outStockCommand);
    }

    public static void removeOutStockCommand() {
        commandThreadLocal.remove();
    }

    public static OutStockCommand getOutStockCommand() {
        return commandThreadLocal.get();
    }

}
