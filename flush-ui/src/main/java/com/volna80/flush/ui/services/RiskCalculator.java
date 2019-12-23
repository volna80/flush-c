package com.volna80.flush.ui.services;

import com.google.common.collect.Lists;
import com.volna80.betfair.api.model.CurrentOrderSummary;
import com.volna80.betfair.api.model.MarketBook;
import com.volna80.betfair.api.model.PriceSize;
import com.volna80.betfair.api.model.Side;
import com.volna80.betfair.api.model.adapter.Precision;
import com.volna80.flush.ui.marketdata.IMDLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RiskCalculator {

    private static final int NUM_OF_ITERATIONS = 20;
    private static Logger logger = LoggerFactory.getLogger(RiskCalculator.class);

    /**
     * calculate not matched bets (reserved money)
     */
    public static Map<Side, Integer> calcNotMatchedMoney(Collection<CurrentOrderSummary> orders) {
        Map<Side, Integer> result = makesidemap();

        for (CurrentOrderSummary order : orders) {
            if (order.getSide() == Side.BACK) {
                int money = order.getSizeRemaining();
                add(result, money, Side.BACK);
            } else {
                final int money = (int) (order.getSizeRemaining() * (Precision.toExternal(order.getPriceSize().getPrice() - Precision.toInternal(1))));
                add(result, money, Side.LAY);
            }
        }


        return result;
    }


    /**
     * calculate a max possible lost for matched bets
     */
    public static int maxLost(Collection<CurrentOrderSummary> matchedOrders, Selection selection) {

        //possible loose for every bet
        Map<Long, Map<Side, Integer>> lost = new HashMap<>();
        lost.put(selection.s1, makesidemap());
        lost.put(selection.s2, makesidemap());
        lost.put(selection.s3, makesidemap());

        for (CurrentOrderSummary order : matchedOrders) {
            if (order.getSide() == Side.BACK) {
                int money = order.getSizeMatched();
                add(lost.get(order.getSelectionId()), money, Side.BACK);
            } else {
                //why int?
                final int money = (int) (order.getSizeMatched() * (Precision.toExternal(order.getAveragePriceMatched() - Precision.toInternal(1))));
                add(lost.get(order.getSelectionId()), money, Side.LAY);
            }
        }

        //possible win for every bet
        Map<Long, Map<Side, Integer>> win = new HashMap<>();
        win.put(selection.s1, makesidemap());
        win.put(selection.s2, makesidemap());
        win.put(selection.s3, makesidemap());

        for (CurrentOrderSummary order : matchedOrders) {
            if (order.getSide() == Side.BACK) {
                int money = (int) (order.getSizeMatched() * (Precision.toExternal(order.getAveragePriceMatched() - Precision.toInternal(1))));
                add(win.get(order.getSelectionId()), money, Side.BACK);
            } else {
                final int money = order.getSizeMatched();
                add(win.get(order.getSelectionId()), money, Side.LAY);
            }
        }

        int maxLost1 = maxLost(selection.s1, selection.s2, selection.s3, lost, win);
        int maxLost2 = maxLost(selection.s2, selection.s1, selection.s3, lost, win);
        int maxLost3 = maxLost(selection.s3, selection.s1, selection.s2, lost, win);

        return Math.min(maxLost1, Math.min(maxLost2, maxLost3));
    }

    private static int maxLost(long winner, long looser1, long looser2, Map<Long, Map<Side, Integer>> lost, Map<Long, Map<Side, Integer>> win) {
        int totalWin = 0;
        totalWin += win.get(winner).get(Side.BACK);
        totalWin += win.get(looser1).get(Side.LAY);
        totalWin += win.get(looser2).get(Side.LAY);

        int totalLost = 0;
        totalLost += lost.get(winner).get(Side.LAY);
        totalLost += lost.get(looser1).get(Side.BACK);
        totalLost += lost.get(looser2).get(Side.BACK);

        return totalWin - totalLost;
    }

    private static Map<Side, Integer> makesidemap() {
        IdentityHashMap<Side, Integer> result = new IdentityHashMap<>();
        result.put(Side.BACK, 0);
        result.put(Side.LAY, 0);
        return result;
    }


    private static void add(Map<Side, Integer> result, int money, Side lay) {
        result.put(lay, result.get(lay) + money);
    }

    /**
     * @return current pnl if we close the position
     */
    public static int pnl(List<CurrentOrderSummary> orders, MarketBook md, Selection selection) {
        logger.trace("calculate pnl:{}:{}", orders, md);

        if (orders.size() == 0) {
            return 0;
        }

        //bottom line
        var maxSize = orders.stream().collect(Collectors.groupingBy(summary -> summary.getSelectionId() + "#" + summary.getSide(),
                Collectors.reducing(0, CurrentOrderSummary::getSizeMatched, Integer::sum))).values().stream().reduce(Integer::max);

        if (maxSize.isEmpty()) {
            return 0;
        }

        final int maxSizeValue = maxSize.get() * 2;

        int sizeStep = maxSizeValue / NUM_OF_ITERATIONS;

        logger.debug("maxSize={}, sizeStep={}", maxSizeValue, sizeStep);

        //first simple impl, just try to buy/sell every side by step by step and find a local max

        boolean firstRun = true;
        int result = 0;
        int bestA = 0, bestB = 0, bestC = 0;

        int startA = bestA - sizeStep * NUM_OF_ITERATIONS;
        int stopA = bestA + sizeStep * NUM_OF_ITERATIONS;
        int startB = bestB - sizeStep * NUM_OF_ITERATIONS;
        int stopB = bestB + sizeStep * NUM_OF_ITERATIONS;
        int startC = bestC - sizeStep * NUM_OF_ITERATIONS;
        int stopC = bestC + sizeStep * NUM_OF_ITERATIONS;

        if (logger.isDebugEnabled()) {
            logger.debug("first cycle = step = {}, a = {} - {}, b = {} - {}, c = {} - {}", sizeStep, startA, stopA, startB, stopB, startC, stopC);
        }

        while (true) {
            int lastRunResult = 0;

            for (int a = startA; a <= stopA; a += sizeStep) {
                for (int b = startB; b <= stopB; b += sizeStep) {
                    for (int c = startC; c <= stopC; c += sizeStep) {

                        Order orderA = Order.make(selection.s1, a > 0 ? Side.BACK : Side.LAY, Math.abs(a));
                        Order orderB = Order.make(selection.s2, b > 0 ? Side.BACK : Side.LAY, Math.abs(b));
                        Order orderC = Order.make(selection.s3, c > 0 ? Side.BACK : Side.LAY, Math.abs(c));

                        final int lost = pnl(orders, md, selection, orderA, orderB, orderC);

                        if (lost <= lastRunResult) {
                            continue;
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("new best pnl: " + lost + " > " + lastRunResult + "[" + result + "]." + orderA + ", " + orderB + ", " + orderC);
                        }
                        bestA = a;
                        bestB = b;
                        bestC = c;
                        lastRunResult = lost;
                    }
                }
            }

            if (firstRun) {
                firstRun = false;
                result = lastRunResult;
            } else if (result >= lastRunResult) {
                //no better result, exit
                return result;
            }

            result = lastRunResult;

            sizeStep = sizeStep / 4;
            if (sizeStep == 0) {
                //no way to reduce the size
                return result;
            }
            startA = bestA - sizeStep * NUM_OF_ITERATIONS;
            stopA = bestA + sizeStep * NUM_OF_ITERATIONS;
            startB = bestB - sizeStep * NUM_OF_ITERATIONS;
            stopB = bestB + sizeStep * NUM_OF_ITERATIONS;
            startC = bestC - sizeStep * NUM_OF_ITERATIONS;
            stopC = bestC + sizeStep * NUM_OF_ITERATIONS;


            if (logger.isDebugEnabled()) {
                logger.debug("next cycle = step = {}, a = {} - {}, b = {} - {}, c = {} - {}", sizeStep, startA, stopA, startB, stopB, startC, stopC);
            }

        }

    }

    private static int pnl(List<CurrentOrderSummary> orders, MarketBook md, Selection selection, Order... newOrders) {
        List<CurrentOrderSummary> list = Lists.newArrayList(orders);
        for (Order order : newOrders) {
            //TODO at the moment, we ignore the size in the market book
            final int price = getBestPrice(md, order.runner, order.side);
            if (price == 0) {
                return Integer.MIN_VALUE;
            }
            list.add(makeOrder(order.runner, order.side, order.size, price));
        }

        return maxLost(list, selection);
    }

    private static CurrentOrderSummary makeOrder(long s1, Side side1, int size1, int bestPrice1) {
        CurrentOrderSummary newOrder = new CurrentOrderSummary();
        newOrder.setSide(side1);
        newOrder.setSelectionId(s1);
        newOrder.setPriceSize(new PriceSize(bestPrice1, size1));
        newOrder.setSizeMatched(size1);
        newOrder.setAveragePriceMatched(bestPrice1);
        return newOrder;
    }

    public static int getBestPrice(MarketBook md, long selection, Side side) {
        return md.getRunners().stream()
                .filter(r -> r.getSelectionId() == selection)
                .findFirst().map(r -> IMDLevel.getBestPrice(r.getEx(), IMDLevel.Type.STANDARD, side)).orElse(0);
    }

    private static class Order {
        final long runner;
        final Side side;
        final int size;

        private Order(long runner, Side side, int size) {
            this.runner = runner;
            this.side = side;
            this.size = size;
        }

        static Order make(long runner, Side side, int size) {
            return new Order(runner, side, size);
        }

        @Override
        public String toString() {
            return "Order{" +
                    "runner=" + runner +
                    ", side=" + side +
                    ", size=" + size +
                    '}';
        }
    }

    public static class Selection {

        public final long s1, s2, s3;
        final long[] s;

        private Selection(long s1, long s2, long s3) {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            s = new long[]{s1, s2, s3};
        }

        public static Selection valueOf(long selection1, long selection2, long selection3) {
            return new Selection(selection1, selection2, selection3);
        }
    }
}
