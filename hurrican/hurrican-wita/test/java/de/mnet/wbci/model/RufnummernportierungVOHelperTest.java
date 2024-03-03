/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.13
 */
package de.mnet.wbci.model;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RufnummernportierungVOHelperTest {

    @Test
    public void testConvertToRufnummerportierungEinzel() throws Exception {
        final boolean alleRufnummern = false;
        final String onkz = "89";
        final String rufnummer = "1234565";
        final String rufnummer2 = "1234566";
        final String pkiAbg = "D001";

        Rufnummernportierung orginalRufnummerportierung = new RufnummernportierungEinzelnTestBuilder()
                .withAlleRufnummernPortieren(alleRufnummern)
                .withRufnummerOnkzs(Arrays.asList(
                        new RufnummerOnkzTestBuilder().withOnkz(onkz).withRufnummer(rufnummer).build(),
                        new RufnummerOnkzTestBuilder().withOnkz(onkz).withRufnummer(rufnummer2).build()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        RufnummernportierungVO rrnpVo = new RufnummernportierungVO();
        rrnpVo.setDnBase(rufnummer);
        rrnpVo.setOnkz(onkz);
        rrnpVo.setPkiAbg(pkiAbg);

        RufnummernportierungVO rrnpVo2 = new RufnummernportierungVO();
        rrnpVo2.setDnBase(rufnummer2);
        rrnpVo2.setOnkz(onkz);
        rrnpVo2.setPkiAbg(pkiAbg);

        RufnummernportierungVO rrnpUnvalid = new RufnummernportierungVO();
        rrnpUnvalid.setDnBase(rufnummer2);
        rrnpUnvalid.setOnkz(onkz);
        rrnpUnvalid.setPkiAbg(pkiAbg);
        rrnpUnvalid.setDirectDial("0");
        rrnpUnvalid.setBlockFrom("0");
        rrnpUnvalid.setBlockTo("100");

        Rufnummernportierung result = RufnummernportierungVOHelper.convertToRuemVaRufnummerportierung(
                Arrays.asList(rrnpVo, rrnpVo2, rrnpUnvalid),
                new WbciGeschaeftsfallKueMrnTestBuilder().withRufnummernportierung(orginalRufnummerportierung)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
        );

        RufnummernportierungEinzeln resultEinzeln = (RufnummernportierungEinzeln) result;

        Assert.assertEquals(result.getTyp(), RufnummernportierungTyp.EINZEL);
        Assert.assertFalse(resultEinzeln.getAlleRufnummernPortieren());
        Assert.assertEquals(resultEinzeln.getRufnummernOnkz().size(), 2);
        Assert.assertNotNull(resultEinzeln.getPortierungskennungPKIauf());
        Assert.assertNotNull(resultEinzeln.getPortierungszeitfenster());

        assertRufnummerOnkz(resultEinzeln.getRufnummernOnkz(), onkz, rufnummer, pkiAbg);
        assertRufnummerOnkz(resultEinzeln.getRufnummernOnkz(), onkz, rufnummer2, pkiAbg);
    }

    private void assertRufnummerOnkz(List<RufnummerOnkz> rufnummernOnkzList, String onkz, String rufnummer,
            String pkiAbg) {
        boolean found = false;
        for (RufnummerOnkz rufnummerOnkz : rufnummernOnkzList) {
            if (onkz.equals(rufnummerOnkz.getOnkz())
                    && rufnummer.equals(rufnummerOnkz.getRufnummer())
                    && pkiAbg.equals(rufnummerOnkz.getPortierungskennungPKIabg())) {
                found = true;
            }
        }
        Assert.assertEquals(found, true);
    }


    @Test
    public void testConvertToRufnummerportierungAnlage() throws Exception {
        final String onkz = "89";
        final String rufnummer = "1234566";
        final String directDial = "0";
        final String[] block = { "0", "20" };
        final String[] block2 = { "0", "50" };
        final String pkiAbg = "D001";

        Rufnummernportierung orginalRufnummerportierung = new RufnummernportierungAnlageTestBuilder()
                .withOnkz(onkz)
                .withDurchwahlnummer(rufnummer)
                .withAbfragestelle(directDial)
                .addRufnummernblock(new RufnummernblockTestBuilder().withRnrBlockVon(block[0]).withRnrBlockBis(block[1]).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .addRufnummernblock(new RufnummernblockTestBuilder().withRnrBlockVon(block2[0]).withRnrBlockBis(block2[1]).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        RufnummernportierungVO rrnpVo = new RufnummernportierungVO();
        rrnpVo.setDnBase(rufnummer);
        rrnpVo.setOnkz(onkz);
        rrnpVo.setPkiAbg(pkiAbg);
        rrnpVo.setDirectDial(directDial);
        rrnpVo.setBlockFrom(block[0]);
        rrnpVo.setBlockTo(block[1]);

        RufnummernportierungVO rrnpVo2 = new RufnummernportierungVO();
        rrnpVo2.setDnBase(rufnummer);
        rrnpVo2.setOnkz(onkz);
        rrnpVo2.setPkiAbg(pkiAbg);
        rrnpVo2.setDirectDial(directDial);
        rrnpVo2.setBlockFrom(block2[0]);
        rrnpVo2.setBlockTo(block2[1]);

        RufnummernportierungVO rrnpUnvalid = new RufnummernportierungVO();
        rrnpUnvalid.setDnBase("5151455");
        rrnpUnvalid.setOnkz(onkz);
        rrnpUnvalid.setPkiAbg(pkiAbg);

        Rufnummernportierung result = RufnummernportierungVOHelper.convertToRuemVaRufnummerportierung(
                Arrays.asList(rrnpVo, rrnpVo2, rrnpUnvalid),
                new WbciGeschaeftsfallKueMrnTestBuilder().withRufnummernportierung(orginalRufnummerportierung)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN)
        );

        RufnummernportierungAnlage resultAnlage = (RufnummernportierungAnlage) result;

        Assert.assertEquals(result.getTyp(), RufnummernportierungTyp.ANLAGE);
        Assert.assertEquals(resultAnlage.getDurchwahlnummer(), rufnummer);
        Assert.assertEquals(resultAnlage.getOnkz(), onkz);
        Assert.assertEquals(resultAnlage.getAbfragestelle(), directDial);
        Assert.assertNotNull(resultAnlage.getPortierungskennungPKIauf());
        Assert.assertNotNull(resultAnlage.getPortierungszeitfenster());
        Assert.assertEquals(resultAnlage.getRufnummernbloecke().size(), 2);

        assertRufnummernBlock(resultAnlage.getRufnummernbloecke(), block[0], block[1], pkiAbg);
        assertRufnummernBlock(resultAnlage.getRufnummernbloecke(), block2[0], block2[1], pkiAbg);
    }

    private void assertRufnummernBlock(List<Rufnummernblock> rufnummernBloecke, String from, String to,
            String pkiAbg) {
        boolean found = false;
        for (Rufnummernblock block : rufnummernBloecke) {
            if (from.equals(block.getRnrBlockVon())
                    && to.equals(block.getRnrBlockBis())
                    && pkiAbg.equals(block.getPortierungskennungPKIabg())) {
                found = true;
            }
        }
        Assert.assertEquals(found, true);
    }

}
