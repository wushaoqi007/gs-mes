package com.greenstone.mes.material.infrastructure.enums;

import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 零件操作、动作和阶段，只有在这里出现的组合才是允许操作的
 *
 * @author gu_renkai
 * @date 2024/12/3 14:02
 */
@Getter
public enum StockBehavior {
    // 订单
    ORDER_CREATE(101, "采购下单", BillOperation.ORDER_CREATE, StockAction.IN, WarehouseStage.WAIT_RECEIVE),
    // 收货单-正常收货
    RECEIVE_SEND(201, "正常送货", BillOperation.RECEIVE_CREATE, StockAction.OUT, WarehouseStage.WAIT_RECEIVE),
    RECEIVE(202, "正常收货", BillOperation.RECEIVE_CREATE, StockAction.IN, WarehouseStage.WAIT_CHECK),
    // 收货单-表处收货
    RECEIVE_TREAT_SEND(203, "表处送货", BillOperation.RECEIVE_TREAT_CREATE, StockAction.OUT, WarehouseStage.TREATING),
    RECEIVE_TREAT(204, "表处收货", BillOperation.RECEIVE_TREAT_CREATE, StockAction.IN, WarehouseStage.WAIT_CHECK),
    // 收货单-返工收货
    RECEIVE_REWORKING_SEND(205, "返工送货", BillOperation.RECEIVE_REWORKED_CREATE, StockAction.OUT, WarehouseStage.REWORKING),
    RECEIVE_REWORKING(206, "返工收货", BillOperation.RECEIVE_REWORKED_CREATE, StockAction.IN, WarehouseStage.WAIT_CHECK),
    // 质检合格单-待入库
    CHECK_OK_TAKE(301, "质检取件", BillOperation.CHECKED_OK_CREATE, StockAction.OUT, WarehouseStage.WAIT_CHECK),
    CHECK_OK(302, "质检合格", BillOperation.CHECKED_OK_CREATE, StockAction.IN, WarehouseStage.CHECKED_OK),
    // 质检表处单-待表处
    CHECK_TREAT_TAKE(303, "质检取件", BillOperation.CHECKED_TREAT_CREATE, StockAction.OUT, WarehouseStage.WAIT_CHECK),
    CHECK_TREAT(304, "质检待表处", BillOperation.CHECKED_TREAT_CREATE, StockAction.IN, WarehouseStage.WAIT_TREAT_SURFACE),
    // 质检NG单-返工中
    CHECK_NG_TAKE(305, "质检取件", BillOperation.CHECKED_NG_CREATE, StockAction.OUT, WarehouseStage.WAIT_CHECK),
    CHECK_NG(306, "质检NG", BillOperation.CHECKED_NG_CREATE, StockAction.IN, WarehouseStage.REWORKING),
    // 合格品入库单
    CHECKED_OK_TAKE(401, "入库取件", BillOperation.OK_IN_STOCK_CREATE, StockAction.OUT, WarehouseStage.CHECKED_OK),
    OK_IN_STOCK(402, "良品入库", BillOperation.OK_IN_STOCK_CREATE, StockAction.IN, WarehouseStage.GOOD),
    // 表面处理单
    TREAT_TAKE(501, "表处取件", BillOperation.TREAT_SURFACE_CREATE, StockAction.OUT, WarehouseStage.WAIT_TREAT_SURFACE),
    TREATING(502, "表面处理", BillOperation.TREAT_SURFACE_CREATE, StockAction.IN, WarehouseStage.TREATING),
    // 领料单
    USED(601, "生产领用", BillOperation.USED_CREATE, StockAction.OUT, WarehouseStage.GOOD),
    // 库存变更单
    STOCK_CHANGE_IN(701, "变更入库", BillOperation.STOCK_CHANGE_CREATE, StockAction.IN, null),
    STOCK_CHANGE_OUT(702, "变更出库", BillOperation.STOCK_CHANGE_CREATE, StockAction.OUT, null),
    // 出库单
    STOCK_OUT(801, "出库单出库", BillOperation.STOCK_OUT_CREATE, StockAction.OUT, null),
    ;

    private final Integer id;

    private final String name;

    private final BillOperation operation;

    private final WarehouseStage stage;

    private final StockAction action;

    StockBehavior(Integer id, String name, BillOperation operation, StockAction action, WarehouseStage stage) {
        this.id = id;
        this.operation = operation;
        this.name = name;
        this.action = action;
        this.stage = stage;
    }

    public static StockBehavior getOrThrow(BillOperation operation, StockAction action) {
        return StockBehavior.get(operation, action).orElseThrow(() -> new ServiceException("操作失败：不能执行该操作"));
    }

    public static Optional<StockBehavior> get(BillOperation operation, StockAction action) {
        for (StockBehavior value : StockBehavior.values()) {
            if (operation == value.operation &&
                    (value.getAction() == null || action == value.getAction())) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static StockBehavior getById(Integer id) {
        return Arrays.stream(StockBehavior.values()).filter(s -> Objects.equals(s.getId(), id)).findFirst().orElse(null);
    }

}
