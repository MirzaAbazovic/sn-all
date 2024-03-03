/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci.helper;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;

/**
 *
 */
public class RufnummernportierungHelperTest {

    private RufnummerPortierungSelection einzelSelection_1 = new RufnummerPortierungSelection(new RufnummerBuilder()
            .withOnKz("089")
            .withDnBase("123")
            .build());

    private RufnummerPortierungSelection einzelSelection_2 = new RufnummerPortierungSelection(new RufnummerBuilder()
            .withOnKz("089")
            .withDnBase("124")
            .build());

    private RufnummerPortierungSelection anlageSelection_1 = new RufnummerPortierungSelection(new RufnummerBuilder()
            .withOnKz("089")
            .withDnBase("123")
            .withDirectDial("0")
            .withRangeFrom("00")
            .withRangeTo("49")
            .build());

    private RufnummerPortierungSelection anlageSelection_2 = new RufnummerPortierungSelection(new RufnummerBuilder()
            .withOnKz("089")
            .withDnBase("123")
            .withDirectDial("0")
            .withRangeFrom("50")
            .withRangeTo("99")
            .build());

    @Test
    public void testGetRufnummernportierungEinzelrufnummer() throws Exception {
        List<RufnummerPortierungSelection> rufnummernSelections = Arrays.asList(einzelSelection_1, einzelSelection_2);

        Rufnummernportierung portierung = RufnummernportierungHelper.getRufnummernportierung(Portierungszeitfenster.ZF1, rufnummernSelections, false);
        Assert.assertEquals(portierung.getPortierungszeitfenster(), Portierungszeitfenster.ZF1);
        Assert.assertEquals(portierung.getTyp(), RufnummernportierungTyp.EINZEL);
        Assert.assertFalse(((RufnummernportierungEinzeln) portierung).getAlleRufnummernPortieren());

        List<RufnummerOnkz> rufnummernOnkzList = ((RufnummernportierungEinzeln) portierung).getRufnummernOnkz();
        Assert.assertEquals(rufnummernOnkzList.size(), 2L);
        Assert.assertEquals(rufnummernOnkzList.get(0).getRufnummer(), einzelSelection_1.getRufnummer().getDnBase());
        Assert.assertEquals(rufnummernOnkzList.get(1).getRufnummer(), einzelSelection_2.getRufnummer().getDnBase());
    }

    @Test
    public void testGetRufnummernportierungAnlageanschluss() throws Exception {
        List<RufnummerPortierungSelection> rufnummernSelections = Arrays.asList(anlageSelection_1, anlageSelection_2);

        Rufnummernportierung portierung = RufnummernportierungHelper.getRufnummernportierung(Portierungszeitfenster.ZF2, rufnummernSelections, false);
        Assert.assertEquals(portierung.getPortierungszeitfenster(), Portierungszeitfenster.ZF2);
        Assert.assertEquals(portierung.getTyp(), RufnummernportierungTyp.ANLAGE);
        Assert.assertEquals(((RufnummernportierungAnlage) portierung).getOnkz(), "89");
        Assert.assertEquals(((RufnummernportierungAnlage) portierung).getDurchwahlnummer(), "123");
        Assert.assertEquals(((RufnummernportierungAnlage) portierung).getAbfragestelle(), "0");

        List<Rufnummernblock> rufnummernBlockList = ((RufnummernportierungAnlage) portierung).getRufnummernbloecke();
        Assert.assertEquals(rufnummernBlockList.size(), 2L);
        Assert.assertEquals(rufnummernBlockList.get(0).getRnrBlockVon(), anlageSelection_1.getRufnummer().getRangeFrom());
        Assert.assertEquals(rufnummernBlockList.get(0).getRnrBlockBis(), anlageSelection_1.getRufnummer().getRangeTo());
        Assert.assertEquals(rufnummernBlockList.get(1).getRnrBlockVon(), anlageSelection_2.getRufnummer().getRangeFrom());
        Assert.assertEquals(rufnummernBlockList.get(1).getRnrBlockBis(), anlageSelection_2.getRufnummer().getRangeTo());
    }

    @Test
    public void testGetRufnummernportierungInvalidMix() throws Exception {
        try {
            List<RufnummerPortierungSelection> rufnummernSelections = Arrays.asList(einzelSelection_1, anlageSelection_1, anlageSelection_2);
            RufnummernportierungHelper.getRufnummernportierung(Portierungszeitfenster.ZF2, rufnummernSelections, false);
            Assert.fail("Missing exception due to invalid rufnummern mix.");
        }
        catch (IllegalStateException e) {
            Assert.assertEquals(e.getMessage(), "Mischung aus Einzelrufnummer und Rufnummernblock ist nicht erlaubt!");
        }
    }

    @Test
    public void testGetRufnummernportierungAnlageDifferentDNbase() throws Exception {
        RufnummerPortierungSelection anlageSelection_3 = new RufnummerPortierungSelection(new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("124")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("49")
                .build());

        performAnlageanschlussMismatchTest(anlageSelection_3, "DN-Base");
    }

    @Test
    public void testGetRufnummernportierungAnlageDifferentOnkz() throws Exception {
        RufnummerPortierungSelection anlageSelection_3 = new RufnummerPortierungSelection(new RufnummerBuilder()
                .withOnKz("0821")
                .withDnBase("123")
                .withDirectDial("0")
                .withRangeFrom("00")
                .withRangeTo("49")
                .build());

        performAnlageanschlussMismatchTest(anlageSelection_3, "Onkz");
    }

    @Test
    public void testGetRufnummernportierungAnlageDifferentDirectDial() throws Exception {
        RufnummerPortierungSelection anlageSelection_3 = new RufnummerPortierungSelection(new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123")
                .withDirectDial("5")
                .withRangeFrom("00")
                .withRangeTo("49")
                .build());

        performAnlageanschlussMismatchTest(anlageSelection_3, "Direct Dial");
    }

    /**
     * Perform error test on rufnummern selection with anlage attribute mismatch.
     *
     * @param anlageSelection_3
     * @param failedAttribute
     * @throws Exception
     */
    private void performAnlageanschlussMismatchTest(RufnummerPortierungSelection anlageSelection_3, String failedAttribute) throws Exception {
        try {
            List<RufnummerPortierungSelection> rufnummernSelections = Arrays.asList(anlageSelection_1, anlageSelection_2, anlageSelection_3);
            RufnummernportierungHelper.getRufnummernportierung(Portierungszeitfenster.ZF2, rufnummernSelections, false);
            Assert.fail(String.format("Missing exception due to different %s.", failedAttribute));
        }
        catch (IllegalStateException e) {
            Assert.assertEquals(e.getMessage(), String.format("Mindestens ein Rufnummernblock hat eine abweichende %s", failedAttribute));
        }
    }
}
