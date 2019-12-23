package com.volna80.betfair.api.model.adapter;

import org.junit.Assert;
import org.junit.Test;

import static com.volna80.betfair.api.model.adapter.Precision.PRECISION;

/**
 * <p>
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class PrecisionTest {

    @Test
    public void toInternal() {

        Assert.assertEquals(2, Precision.toInternal(0.02));
        Assert.assertEquals(2, Precision.toInternal(0.0201));
        Assert.assertEquals(-2, Precision.toInternal(-0.02));
        Assert.assertEquals(-2, Precision.toInternal(-0.0201));

        Assert.assertEquals(180, Precision.toInternal(1.80));
        Assert.assertEquals(181, Precision.toInternal(1.81));
        Assert.assertEquals(181, Precision.toInternal(1.811));
        Assert.assertEquals(182, Precision.toInternal(1.819));
        Assert.assertEquals(182, Precision.toInternal(1.82));


    }

    @Test
    public void toExternal() {
        Assert.assertEquals(0.02, Precision.toExternal(2), PRECISION);
        Assert.assertEquals(-0.02, Precision.toExternal(-2), PRECISION);
    }

    @Test
    public void round() {
        Assert.assertEquals(0.02, Precision.round(0.02000001), PRECISION);
        Assert.assertEquals(0.02, Precision.round(0.01999999), PRECISION);
    }

    @Test
    public void toUI() {
        Assert.assertEquals("2.02", Precision.toUI(202));
        Assert.assertEquals("2.02", Precision.toUI(202, 2));
        Assert.assertEquals("2.0", Precision.toUI(202, 1));
        Assert.assertEquals("2.1", Precision.toUI(209, 1));
        Assert.assertEquals("2", Precision.toUI(202, 0));
    }

}
