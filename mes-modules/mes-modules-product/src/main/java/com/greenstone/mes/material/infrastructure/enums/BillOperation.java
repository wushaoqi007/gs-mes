package com.greenstone.mes.material.infrastructure.enums;

import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.*;

import java.util.Arrays;
import java.util.List;

/**
 * 零件操作
 *
 * @author gu_renkai
 * @date 2022/12/14 9:44
 */
@Getter
public enum BillOperation {
    /**
     * 0 创建订单
     */
    ORDER_CREATE(0, "创建订单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_RECEIVE);
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_RECEIVE).action(StockAction.IN).auto(true).build()
            );
        }
    },
    /**
     * 1 创建收货单
     */
    RECEIVE_CREATE(1, "创建收货单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.WAIT_RECEIVE, StockAction.OUT);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_RECEIVE).action(StockAction.OUT).auto(true).build(),
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.IN).auto(false).build()
            );
        }
    },
    /**
     * 3 创建质检合格单
     */
    CHECKED_OK_CREATE(3, "创建质检合格单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.CHECKED_OK, StockAction.IN);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.OUT).auto(false).build(),
                    StageAction.builder().stage(WarehouseStage.CHECKED_OK).action(StockAction.IN).auto(true).build()
            );
        }
    },

    /**
     * 4 创建质检合格表处单
     */
    CHECKED_TREAT_CREATE(4, "创建质检合格表处单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.WAIT_TREAT_SURFACE, StockAction.IN);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.OUT).auto(false).build(),
                    StageAction.builder().stage(WarehouseStage.WAIT_TREAT_SURFACE).action(StockAction.IN).auto(true).build()
            );
        }
    },
    /**
     * 5 创建质检NG单
     */
    CHECKED_NG_CREATE(5, "创建质检NG单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.REWORKING, StockAction.IN);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.OUT).auto(false).build(),
                    StageAction.builder().stage(WarehouseStage.REWORKING).action(StockAction.IN).auto(true).build()
            );
        }
    },

    /**
     * 6 创建表处单
     */
    TREAT_SURFACE_CREATE(6, "创建表处单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_TREAT_SURFACE);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.TREATING, StockAction.IN);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.WAIT_TREAT_SURFACE).action(StockAction.OUT).auto(true).build(),
                    StageAction.builder().stage(WarehouseStage.TREATING).action(StockAction.IN).auto(true).build()
            );
        }
    },

    /**
     * 7 创建表处收货单
     */
    RECEIVE_TREAT_CREATE(7, "创建表处收货单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.TREATING, StockAction.OUT);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.TREATING).action(StockAction.OUT).auto(true).build(),
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.IN).auto(false).build()
            );
        }
    },

    /**
     * 8 创建返工单
     * 质检人员质检NG后，直接操作零件为返工中，返工操作不再需要
     */
    @Deprecated
    REWORK(8, "创建返工单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_REWORKED);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.REWORKING, StockAction.IN);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of();
        }
    },
    /**
     * 9 创建返工收货单
     */
    RECEIVE_REWORKED_CREATE(9, "创建返工收货单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.WAIT_CHECK);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.REWORKING, StockAction.OUT);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.REWORKING).action(StockAction.OUT).auto(true).build(),
                    StageAction.builder().stage(WarehouseStage.WAIT_CHECK).action(StockAction.IN).auto(false).build()
            );
        }
    },

    /**
     * 10 创建合格品入库单
     */
    OK_IN_STOCK_CREATE(10, "创建合格品入库单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of(WarehouseStage.GOOD);
        }

        @Override
        public StageAction getDefaultAction() {
            return new StageAction(WarehouseStage.CHECKED_OK, StockAction.OUT);
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.CHECKED_OK).action(StockAction.OUT).auto(true).build(),
                    StageAction.builder().stage(WarehouseStage.GOOD).action(StockAction.IN).auto(false).build()
            );
        }
    },

    /**
     * 11 创建领用单
     */
    USED_CREATE(11, "创建领用单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return null;
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of();
        }
    },

    /**
     * 12 创建退料单
     */
    @Deprecated
    RETURN(12, "创建退料单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return null;
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of();
        }
    },

    /**
     * 13 创建调拨单
     */
    @Deprecated
    TRANSFER_OUT(13, "创建调拨单", StockAction.OUT) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return null;
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of();
        }
    },

    /**
     * 14 创建出库单
     */
    STOCK_OUT_CREATE(14, "创建出库单", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return null;
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of(
                    StageAction.builder().stage(WarehouseStage.GOOD).action(StockAction.OUT).auto(false).build()
            );
        }
    },

    /**
     * 15 创建库存变更单
     */
    STOCK_CHANGE_CREATE(15, "库存变更", StockAction.IN) {
        @Override
        public List<WarehouseStage> getValidStages() {
            return List.of();
        }

        @Override
        public StageAction getDefaultAction() {
            return null;
        }

        @Override
        public List<StageAction> getStageActions() {
            return List.of();
        }
    };

    private final int id;

    private final String name;

    private final StockAction action;

    BillOperation(int id, String name, StockAction action) {
        this.id = id;
        this.name = name;
        this.action = action;
    }

    public static BillOperation getByIdOrThrow(int id) {
        BillOperation operation = getById(id);
        if (operation == null) throw new ServiceException("操作失败：不支持的操作编号：" + id);
        return operation;
    }

    public static BillOperation getById(int id) {
        return Arrays.stream(BillOperation.values()).filter(o -> o.getId() == id).findFirst().orElse(null);
    }

    /**
     * 是否为检验后的入库操作（合格品入库、需要返工、需要表处），需要保存检验记录
     */
    public boolean isInStockAfterCheck() {
        return this == CHECKED_NG_CREATE || this == CHECKED_TREAT_CREATE || this == CHECKED_OK_CREATE;
    }

    /**
     * 可以操作的阶段
     */
    abstract public List<WarehouseStage> getValidStages();

    /**
     * 预设操作
     */
    abstract public StageAction getDefaultAction();

    abstract public List<StageAction> getStageActions();

    public boolean isValidStages(WarehouseStage stage) {
        return this.getValidStages() == null || this.getValidStages().contains(stage);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageAction {
        private WarehouseStage stage;

        private StockAction action;

        /**
         * 如果是自动的操作，则需要找到仓库然后执行相应的操作，否则使用接收到的仓库操作
         */
        private boolean auto;

        public StageAction(WarehouseStage stage, StockAction action) {
            this.stage = stage;
            this.action = action;
        }
    }


}
