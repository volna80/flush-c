package com.volna80.flush.ui.marketdata;

import com.volna80.betfair.api.model.ExchangePrices;
import com.volna80.betfair.api.model.PriceSize;
import com.volna80.betfair.api.model.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public abstract class IMDLevel {


    public static final int MAX_PRICE = 100000;
    public static final int MIN_PRICE = 101;

    //market data price levels
    public static final int[] levels;
    public static final int[] indexByPrice;


    /**
     * Asian Handicap & Total Goal Markets
     * Price
     * <p>
     * Increment
     * 1.01 → 1000	0.01
     */
    public static final int[] levels2 = IntStream.rangeClosed(MIN_PRICE, MAX_PRICE).toArray();
    public static final int[] index2ByPrice;

    static {

        /**
         * Odds Markets
         Price

         Increment
         1.01 → 2	0.01
         2→ 3	0.02
         3 → 4	0.05
         4 → 6	0.1
         6 → 10	0.2
         10 → 20	0.5
         20 → 30	1
         30 → 50	2
         50 → 100	5
         100 → 1000	10
         */

        List<Integer> tmp = new ArrayList<>();

        int step = 1;
        int l = MIN_PRICE;

        while (l <= MAX_PRICE) {
            tmp.add(l);
            l += step;
            switch (l) {
                case 200:
                    step = 2;
                    break;
                case 300:
                    step = 5;
                    break;
                case 400:
                    step = 10;
                    break;
                case 600:
                    step = 20;
                    break;
                case 1000:
                    step = 50;
                    break;
                case 2000:
                    step = 100;
                    break;
                case 3000:
                    step = 200;
                    break;
                case 5000:
                    step = 500;
                    break;
                case 10000:
                    step = 1000;
                    break;
            }
        }

        levels = new int[tmp.size()];
        indexByPrice = new int[MAX_PRICE + 1];
        for (int i = 0; i < tmp.size(); i++) {
            levels[i] = tmp.get(i);
            indexByPrice[tmp.get(i)] = i;
        }

        index2ByPrice = new int[MAX_PRICE + 1];
        for (int i = 0; i < levels2.length; i++) {
            index2ByPrice[levels2[i]] = i;
        }
    }

    public static int getMidPrice(ExchangePrices prices, Type type) {

        int bestYes = prices.getAvailableToLay().stream()
                .filter(ps -> ps.getSize() > 0)
                .map(PriceSize::getPrice)
                .sorted()
                .findFirst().orElse(0);

        int bestYesIndex = type == Type.STANDARD ? indexByPrice[bestYes] : index2ByPrice[bestYes];

        int bestNo = prices.getAvailableToBack().stream()
                .filter(ps -> ps.getSize() > 0)
                .map(PriceSize::getPrice)
                .sorted((o1, o2) -> Integer.compare(o2, o1))
                .findFirst().orElse(0);

        int bestNoIndex = type == Type.STANDARD ? indexByPrice[bestNo] : index2ByPrice[bestNo];


        int midIndex = (bestYesIndex + bestNoIndex) / 2;
        return type == Type.STANDARD ? levels[midIndex] : levels2[midIndex];

    }

    public static int getBestPrice(ExchangePrices prices, Type type, Side side) {

        return side == Side.LAY ? prices.getAvailableToLay().stream()
                .filter(ps -> ps.getSize() > 0)
                .map(PriceSize::getPrice)
                .sorted()
                .findFirst().orElse(0) :

                prices.getAvailableToBack().stream()
                        .filter(ps -> ps.getSize() > 0)
                        .map(PriceSize::getPrice)
                        .sorted((o1, o2) -> Integer.compare(o2, o1))
                        .findFirst().orElse(0);
    }

    public enum Type {
        STANDARD, ASIAN_HANDICAP
    }


}
