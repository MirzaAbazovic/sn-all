/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2004 09:36:56
 */
package de.augustakom.common.tools.lang;

import java.math.*;
import java.text.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * TestCase fuer <code>NumberTools</code>.
 *
 *
 */
public class NumberToolsTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(NumberToolsTest.class);

    //    public void testRound() {
    //        float mtl = 500.47f;
    //        int tageakt = 31;
    //        int tage = 10;
    //
    //        float anteilig = mtl/tageakt * tage;
    //        LOGGER.debug("... anteilig: "+anteilig);
    //        LOGGER.debug(new BigDecimal(anteilig).setScale(2, BigDecimal.ROUND_DOWN).floatValue());
    //    }

    @Test(enabled = false) // Testet nichts?
    public void testFormat() {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");//#0.00");
        LOGGER.debug(df.format(29.96129f));
        LOGGER.debug("-------- " + new Double(85363.805f));
        LOGGER.debug("--------");

        float f1 = 34.09677f;

        BigDecimal bd = new BigDecimal(f1);
        BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        LOGGER.debug(".... " + NumberFormat.getCurrencyInstance().format(bd2.doubleValue()));
        //        LOGGER.debug("bd: " + bd);
        LOGGER.debug("bd2: " + bd2.floatValue());
        LOGGER.debug("bd2-format: " + df.format(bd2.floatValue()));

        //        BigDecimal bdf2 = new BigDecimal(f2);
        //        LOGGER.debug("bdf2: "+bdf2.setScale(2, BigDecimal.ROUND_UP).floatValue());
        //
        //        BigDecimal bdSum = new BigDecimal(f1+f2);
        //        LOGGER.debug("bdf2: "+bdSum.setScale(2, BigDecimal.ROUND_UP).floatValue());

        LOGGER.debug("-----------");

        //        014     double value = 1768.3518;
        //        015     print(value, "#0.0");
        //        016     print(value, "#0.000");
        //        017     print(value, "000000.000");
        //        018     print(value, "#.000000");
        //        019     print(value, "#,###,##0.000");
        //        020     print(value, "0.000E00");
    }

    /**
     * Test fuer die Methode NumberTools#equal(Integer, Integer)
     */
    @Test(groups = { "unit" })
    public void testEqual() {
        Integer i1 = Integer.valueOf(0);
        Integer i2 = Integer.valueOf(0);
        Integer i3 = Integer.valueOf(1);

        boolean r1 = NumberTools.equal(i1, i2);
        Assert.assertTrue(r1, "NumberTools.equal lieferte falsches Ergebnis!");

        boolean r2 = NumberTools.equal(i1, i3);
        Assert.assertFalse(r2, "NumberTools.equal lieferte falsches Ergebnis!");

        boolean r3 = NumberTools.equal(i1, null);
        Assert.assertFalse(r3, "NumberTools.equal lieferte falsches Ergebnis!");
    }

    /**
     * Test fuer die Methode NumberTools#isIn(Number, Number[])
     */
    @Test(groups = { "unit" })
    public void testIsIn() {
        Integer i1 = Integer.valueOf(0);
        Integer i2 = Integer.valueOf(0);
        Integer i3 = Integer.valueOf(1);
        Integer i4 = Integer.valueOf(2);
        Integer i5 = Integer.valueOf(3);
        Integer i6 = Integer.valueOf(4);

        boolean r1 = NumberTools.isIn(i1, new Number[] { i3, i2, i4 });
        Assert.assertTrue(r1, "NumberTools.isIn lieferte falsches Ergebnis!");

        boolean r2 = NumberTools.isIn(i1, new Number[] { i3, i4, i5, i6 });
        Assert.assertFalse(r2, "NumberTools.equal lieferte falsches Ergebnis!");

        boolean r3 = NumberTools.isIn(null, new Number[] { });
        Assert.assertFalse(r3, "NumberTools.equal lieferte falsches Ergebnis!");
    }

    /* Test fuer den Int-Boolean Konverter. */
    @Test(groups = { "unit" })
    public void testConvertInt2Boolean() {
        Integer nil = null;
        Integer intTrue = Integer.valueOf(-1);
        Integer intTrue2 = Integer.valueOf(1);
        Integer intFalse = Integer.valueOf(0);
        Integer intQ = Integer.valueOf(2);

        Assert.assertEquals(NumberTools.convertInt2Boolean(nil), Boolean.FALSE, "nil muss False ergeben");
        Assert.assertEquals(NumberTools.convertInt2Boolean(intTrue), Boolean.TRUE, "-1 muss True ergeben ");
        Assert.assertEquals(NumberTools.convertInt2Boolean(intTrue2), Boolean.TRUE, "1 muss True ergeben");
        Assert.assertEquals(NumberTools.convertInt2Boolean(intFalse), Boolean.FALSE, "0 muss False ergeben");
        Assert.assertEquals(NumberTools.convertInt2Boolean(intQ), Boolean.FALSE, "2 muss False ergeben");
    }

    /* Test fuer NumberTools#roundTo(int, int) */
    @Test(groups = { "unit" })
    public void testRoundToNPlaces() {
        int rounded = NumberTools.roundToNextHundred(1257.5f);
        Assert.assertEquals(rounded, 1300, "Runden auf hunderter funktioniert nicht");
    }
}

