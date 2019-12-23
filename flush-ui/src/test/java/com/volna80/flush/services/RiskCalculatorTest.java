package com.volna80.flush.services;

import com.google.common.collect.Lists;
import com.volna80.betfair.api.model.*;
import com.volna80.flush.ui.services.RiskCalculator;
import org.junit.Test;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class RiskCalculatorTest {

    public static final long SELECTION_1 = 1;
    public static final long SELECTION_2 = 2;
    public static final long SELECTION_3 = 3;

    public static final RiskCalculator.Selection SELECTION = RiskCalculator.Selection.valueOf(SELECTION_1, SELECTION_2, SELECTION_3);

    private static CurrentOrderSummary newOrderSummary(long selection, Side side, int price, int size, int avgPriceMatched, int matched) {
        CurrentOrderSummary summary = new CurrentOrderSummary();
        summary.setSelectionId(selection);
        summary.setSide(side);
        summary.setPriceSize(new PriceSize(price, size));
        summary.setSizeMatched(matched);
        summary.setSizeRemaining(size - matched);
        summary.setAveragePriceMatched(avgPriceMatched);
        return summary;
    }

    @Test
    public void calcNotMatchedMoney() {

        CurrentOrderSummary summary = newOrderSummary(SELECTION_1, Side.BACK, 200, 10, 200, 5);

        List<CurrentOrderSummary> list = Lists.newArrayList(summary);

        Map<Side, Integer> expectedResult = new IdentityHashMap<>();
        expectedResult.put(Side.BACK, 5);
        expectedResult.put(Side.LAY, 0);

        assertEquals(expectedResult, RiskCalculator.calcNotMatchedMoney(list));

    }

    @Test
    public void testMaxLost() {

        CurrentOrderSummary summary = newOrderSummary(SELECTION_1, Side.BACK, 200, 10, 200, 5);

        List<CurrentOrderSummary> list = Lists.newArrayList(summary);
        assertEquals(-5, RiskCalculator.maxLost(list, SELECTION));

    }

    @Test
    public void testMaxLost2() {

        //  3.55 - 10
        CurrentOrderSummary s1 = newOrderSummary(SELECTION_1, Side.BACK, 171, 10, 171, 5);
        // 50.00 - 5
        CurrentOrderSummary s2 = newOrderSummary(SELECTION_2, Side.BACK, 600, 10, 600, 10);
        // -5.00 - 10
        CurrentOrderSummary s3 = newOrderSummary(SELECTION_3, Side.BACK, 350, 10, 350, 0);

        List<CurrentOrderSummary> list = Lists.newArrayList(s1, s2, s3);
        assertEquals(-15, RiskCalculator.maxLost(list, SELECTION));

    }

    @Test
    public void testMaxLost3() {

        //  3.55 + 10
        CurrentOrderSummary s1 = newOrderSummary(SELECTION_1, Side.BACK, 171, 10, 171, 5);
        // -50.00 - 5 -10
        CurrentOrderSummary s2 = newOrderSummary(SELECTION_2, Side.LAY, 600, 10, 600, 10);
        // -5.00 + 10
        CurrentOrderSummary s3 = newOrderSummary(SELECTION_3, Side.BACK, 350, 10, 350, 10);

        List<CurrentOrderSummary> list = Lists.newArrayList(s1, s2, s3);
        assertEquals(-65, RiskCalculator.maxLost(list, SELECTION));

    }

    @Test
    public void testMaxLost4() {

        //  +7 - 6
        CurrentOrderSummary s1 = newOrderSummary(SELECTION_1, Side.BACK, 170, 10, 170, 10);
        // -10 + 10
        CurrentOrderSummary s2 = newOrderSummary(SELECTION_1, Side.LAY, 160, 10, 160, 10);

        List<CurrentOrderSummary> list = Lists.newArrayList(s1, s2);
        assertEquals(0, RiskCalculator.maxLost(list, SELECTION));

    }

    @Test
    public void testMaxLost5() {

        CurrentOrderSummary s1 = newOrderSummary(SELECTION_1, Side.BACK, 170, 1000, 170, 1000);
        CurrentOrderSummary s2 = newOrderSummary(SELECTION_1, Side.LAY, 150, 1200, 150, 1200);

        // 1) win = +7 - 6 = +1
        // 2) lost = -10 + 12 = +2

        List<CurrentOrderSummary> list = Lists.newArrayList(s1, s2);
        assertEquals(100, RiskCalculator.maxLost(list, SELECTION));

    }

    @Test
    public void testPNL1() {

        Runner runner1;
        {
            runner1 = new Runner();
            runner1.setSelectionId(SELECTION_1);
            ExchangePrices ex1 = new ExchangePrices();

            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(140, 30)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(150, 30)));
            runner1.setEx(ex1);
        }

        Runner runner2;
        {
            runner2 = new Runner();
            runner2.setSelectionId(SELECTION_2);
            ExchangePrices ex1 = new ExchangePrices();
            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(220, 30)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(230, 30)));
            runner2.setEx(ex1);
        }

        Runner runner3;
        {
            runner3 = new Runner();
            runner3.setSelectionId(SELECTION_3);
            ExchangePrices ex1 = new ExchangePrices();
            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(400, 30)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(500, 30)));
            runner3.setEx(ex1);
        }

        MarketBook md = new MarketBook();
        md.setRunners(Lists.newArrayList(runner1, runner2, runner3));

        //
        //Order{runner=1, side=LAY, size=2000}, Order{runner=2, side=LAY, size=550}, Order{runner=3, side=LAY, size=250}

        //  +7 - 10 + 5.5 + 2.5 = 500
        //  -10 + 20 - 7,15 + 2.5 = 500
        //  -10 + 20 + 5.5 - 10 = 550

        //Order{runner=1, side=LAY, size=2540}, Order{runner=2, side=LAY, size=918}, Order{runner=3, side=LAY, size=422}

        //+7 - 12.7 + 9.18 + 4.22 = 7.7
        //-10 + 25.4 - 11.934 + 4.22 = 7.69
        //-10 + 25.4 + 9.18 - 16.88 =


        CurrentOrderSummary s1 = newOrderSummary(SELECTION_1, Side.BACK, 170, 1000, 170, 1000);

        List<CurrentOrderSummary> list = Lists.newArrayList(s1);
        assertEquals(769, RiskCalculator.pnl(list, md, SELECTION));

    }

    @Test
    public void testPNL2() {

        Runner runner1;
        {
            runner1 = new Runner();
            runner1.setSelectionId(SELECTION_1);
            ExchangePrices ex1 = new ExchangePrices();


            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(1000, 10)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(1100, 10)));
            runner1.setEx(ex1);
        }

        Runner runner2;
        {
            runner2 = new Runner();
            runner2.setSelectionId(SELECTION_2);
            ExchangePrices ex1 = new ExchangePrices();
            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(470, 10)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(500, 10)));
            runner2.setEx(ex1);
        }

        Runner runner3;
        {
            runner3 = new Runner();
            runner3.setSelectionId(SELECTION_3);
            ExchangePrices ex1 = new ExchangePrices();
            ex1.setAvailableToBack(Lists.newArrayList(new PriceSize(142, 10)));
            ex1.setAvailableToLay(Lists.newArrayList(new PriceSize(143, 10)));
            runner3.setEx(ex1);
        }

        MarketBook md = new MarketBook();
        md.setRunners(Lists.newArrayList(runner1, runner2, runner3));

        //  +7 -3.5
        //  +3.5
        //  +3.5
        CurrentOrderSummary s1 = newOrderSummary(SELECTION_3, Side.BACK, 242, 500, 242, 500);

        List<CurrentOrderSummary> list = Lists.newArrayList(s1);
        //Order{runner=1, side=LAY, size=0}, Order{runner=2, side=LAY, size=0}, Order{runner=3, side=LAY, size=850}

        // -5 + 8.5 = 3.5
        // -5 + 8.5
        // 7.1 - 3.655 = 3.45
        assertEquals(345, RiskCalculator.pnl(list, md, SELECTION));

    }
}
