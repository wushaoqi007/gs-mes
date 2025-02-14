package com.greenstone.mes.oa.infrastructure.util;

import cn.hutool.core.collection.CollectionUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@ToString
public class Periods {

    public static final Periods EMPTY_PERIODS = new Periods();

    private List<Period> periods = new LinkedList<>();

    private Long minOfAll = null;

    private Long maxOfAll = null;

    private boolean sortedAndMerged = false;

    public boolean isNotEmpty() {
        return periods.size() > 0;
    }

    public boolean isEmpty() {
        return periods.isEmpty();
    }

    public Periods() {
    }

    public Periods(long start, long end) {
        this.periods.add(new Period(start, end));
    }

    public int size() {
        return this.periods.size();
    }

    /**
     * 将区段添加到区段列表中
     *
     * @param t1 起始值
     * @param t2 结束值
     */
    public void addPeriod(long t1, long t2) {
        periods.add(Period.builder().start(Math.min(t1, t2)).end(Math.max(t1, t2)).build());
        sortedAndMerged = false;
    }

    /**
     * 找出两个区段列表的交集
     *
     * @param otherOne 另一个区段列表
     * @return 交集
     */
    public Periods intersect(Periods otherOne) {
        return intersection(this, otherOne);
    }

    /**
     * 找出另一个区段列表在本区段列表中的补集
     * 即：属于本区段列表，但不属于另一个区段列表
     *
     * @param otherOne 另一个区段列表
     * @return 补集
     */
    public Periods complement(Periods otherOne) {
        return complement(otherOne, this);
    }

    /**
     * 计算区段列表中所有区段长度的总和
     *
     * @return 区段长度的总和
     */
    public long sum() {
        this.selfSortAndMerge();
        long sum = 0;
        for (Period period : this.getPeriods()) {
            sum += period.getEnd() - period.getStart();
        }
        return sum;
    }

    /**
     * 判断某个时间点是否在区间内
     *
     * @param time 时间点
     * @return 是否在区间内
     */
    public boolean isInPeriods(long time) {
        for (Period period : this.getPeriods()) {
            if (time >= period.getStart() && time <= period.getEnd()) {
                return true;
            }
        }
        return false;
    }

    public long getMinTime() {
        if (this.minOfAll != null) {
            return this.minOfAll;
        }
        long minOfAll = Long.MAX_VALUE;
        if (CollectionUtil.isEmpty(periods)) {
            log.error("no period in list");
            throw new RuntimeException("区段列表内没有区段存在");
        }
        for (Period period : periods) {
            if (minOfAll > period.getStart()) {
                minOfAll = period.getStart();
            }
        }
        this.minOfAll = minOfAll;
        return minOfAll;
    }

    public long getMaxTime() {
        if (this.maxOfAll != null) {
            return this.maxOfAll;
        }
        long maxOfAll = Long.MIN_VALUE;
        if (CollectionUtil.isEmpty(periods)) {
            log.error("no period in list");
            throw new RuntimeException("区段列表内没有区段存在");
        }
        for (Period period : periods) {
            if (maxOfAll < period.getEnd()) {
                maxOfAll = period.getEnd();
            }
        }
        this.maxOfAll = maxOfAll;
        return maxOfAll;
    }

    /**
     * 并集
     *
     * @param periods 待合并区间
     */
    public void merge(Periods periods) {
        List<Period> mergePeriod = periods.getPeriods();
        this.periods.addAll(mergePeriod);
    }


    /**
     * 将区段列表排序并合并
     * 如：1-6 5-11 12-18 合并后 1-11 12-18
     */
    private void selfSortAndMerge() {
        if (!sortedAndMerged) {
            List<Period> uselessPeriod = new ArrayList<>();
            this.periods = this.periods.stream().sorted((o1, o2) -> o1.getStart() > o2.getStart() ? 1 : -1).collect(Collectors.toList());
            for (int i = 0; i < periods.size() - 1; i++) {
                Period period = periods.get(i);
                Period nextPeriod = periods.get(i + 1);
                if (period.getEnd() >= nextPeriod.getStart()) {
                    nextPeriod.setStart(period.getStart());
                    nextPeriod.setEnd(Math.max(period.getEnd(), nextPeriod.getEnd()));
                    uselessPeriod.add(period);
                }
            }
            for (Period period : uselessPeriod) {
                this.periods.remove(period);
            }
        }
    }

    /**
     * 找出两个区段列表的交集
     *
     * @param periods1 区段列表1
     * @param periods2 区段列表2
     * @return 两个区段列表的交集
     */
    private Periods intersection(Periods periods1, Periods periods2) {
        periods1.selfSortAndMerge();
        periods2.selfSortAndMerge();

        Periods result = new Periods();
        for (Period period1 : periods1.getPeriods()) {
            for (Period period2 : periods2.getPeriods()) {
                if (period1.getStart() < period2.getStart() && period1.getEnd() > period2.getEnd()) {
                    result.addPeriod(period2.getStart(), period2.getEnd());
                } else if (period1.getStart() >= period2.getStart() && period1.getStart() <= period2.getEnd()) {
                    result.addPeriod(period1.getStart(), Math.min(period1.getEnd(), period2.getEnd()));
                } else if (period1.getEnd() >= period2.getStart() && period1.getEnd() <= period2.getEnd()) {
                    result.addPeriod(Math.max(period1.getStart(), period2.getStart()), period1.getEnd());
                }
            }
        }
        return result;
    }

    /**
     * 找出两个区段列表的补集
     * 注意：此处结果为 periods1 在 periods2 中的相对补集，即结果区段属于 periods2 但不属于 periods1
     *
     * @param periods1 区段列表1
     * @param periods2 区段列表2
     * @return 两个区段列表的补集
     */
    private Periods complement(Periods periods1, Periods periods2) {
        if (periods1.isEmpty()) {
            return periods2;
        }
        periods1.selfSortAndMerge();
        periods2.selfSortAndMerge();

        List<Integer> maskIndex2 = new ArrayList<>();
        Periods result = new Periods();
        rl:
        for (int i = 0; i < periods1.getPeriods().size(); i++) {
            for (int j = 0; j < periods2.getPeriods().size(); j++) {
                if (maskIndex2.contains(j)) {
                    continue;
                }
                Period period1 = periods1.getPeriods().get(i);
                Period period2 = periods2.getPeriods().get(j);
                if (period1.getStart() > period2.getStart()) {
                    result.addPeriod(period2.getStart(), Math.min(period1.getStart(), period2.getEnd()));
                    if (period1.getEnd() < period2.getEnd()) {
                        i = complement(periods1, i, period2, result);
                    } else if (periods2.size() - 1 == j) {
                        break rl;
                    }
                } else if (period1.getEnd() < period2.getEnd()) {
                    i = complement(periods1, i, period2, result);
                } else if (period1.getEnd() >= period2.getEnd()) {
                    maskIndex2.add(j);
                }
            }
        }
        return result;
    }

    /**
     * 处理 periods1 有多个区段和 period2 有交集的情况
     *
     * @param periods1 区段列表1
     * @param i        区段列表1当前的区段索引
     * @param period2  区段列表2的当前区段
     * @param result   两个区段列表的补集
     */
    private int complement(Periods periods1, int i, Period period2, Periods result) {
        Period period1 = periods1.getPeriods().get(i);
        if (i < periods1.getPeriods().size() - 1) {
            i++;
            Period nextPeriod1 = periods1.getPeriods().get(i);
            if (nextPeriod1.getStart() < period2.getEnd()) {
                if (nextPeriod1.getStart() < period2.getStart()) {
                    complement(periods1, i, period2, result);
                } else {
                    result.addPeriod(Math.max(period2.getStart(), period1.getEnd()), nextPeriod1.getStart());
                }
            } else {
                if (period1.getEnd() < period2.getEnd()) {
                    result.addPeriod(Math.max(period1.getEnd(), period2.getStart()), period2.getEnd());
                }
                return i;
            }
            if (nextPeriod1.getEnd() < period2.getEnd()) {
                complement(periods1, i, period2, result);
            }
        } else {
            if (period1.getEnd() < period2.getEnd()) {
                result.addPeriod(Math.max(period1.getEnd(), period2.getStart()), period2.getEnd());

            }
        }
        if (i < periods1.getPeriods().size() - 1) {
            return ++i;
        } else {
            return i;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Period {

        private long start;

        private long end;

    }

}
