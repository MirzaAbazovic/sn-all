/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder;

import java.util.*;

import org.testng.Assert;
import org.testng.TestException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.TNB;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungVO;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;

@Test(groups = BaseTest.UNIT)
public class RufnummernportierungVOBuilderTest extends BaseTest {

    @Test
    public void compareNumbersAndDefineStatusInfo() throws Exception {

        RufnummernportierungEinzeln rufnummerRequest = new RufnummernportierungEinzelnTestBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("1").build())
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("3").build())
                .build();
        RufnummernportierungEinzeln rufnummerMeldung = new RufnummernportierungEinzelnTestBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("1").build())
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("2").build())
                .build();
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(rufnummerRequest)
                .withAbgebenderEKP(CarrierCode.MNET)
                .build();
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> wbciRequest = new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallKueMrn>()
                .withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn)
                .build();

        RufnummernportierungVOBuilder cut = new RufnummernportierungVOBuilder();

        Collection<Rufnummer> rufnummern = new ArrayList<>();
        rufnummern.add(
                new RufnummerBuilder()
                        .withFutureCarrier("DTAG", "D001")
                        .withDnBase("1")
                        .withOnKz("821")
                        .build()
        );
        rufnummern.add(
                new RufnummerBuilder()
                        .withFutureCarrier("SOME UNKNOWN CARRIER", null)
                        .withDnBase("2")
                        .withOnKz("821")
                        .build()
        );
        rufnummern.add(
                new RufnummerBuilder()
                        .withFutureCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                        .withDnBase("3")
                        .withOnKz("821")
                        .build()
        );

        List<RufnummernportierungVO> result = cut
                .withRufnummernportierungFromMeldung(rufnummerMeldung)
                .withWbciRequest(wbciRequest)
                .withRufnummern(rufnummern)
                .build();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        for (RufnummernportierungVO vo : result) {
            switch (vo.getDnBase()) {
                case "1":
                    Assert.assertEquals(vo.getStatusInfo(), RufnummernportierungVO.StatusInfo.BESTAETIGT);
                    Assert.assertEquals(vo.getPkiAuf(), "D001");
                    break;
                case "2":
                    Assert.assertEquals(vo.getStatusInfo(), RufnummernportierungVO.StatusInfo.NEU_AUS_RUEM_VA);
                    Assert.assertNull(vo.getPkiAuf());
                    break;
                case "3":
                    Assert.assertEquals(vo.getStatusInfo(), RufnummernportierungVO.StatusInfo.NICHT_IN_RUEM_VA);
                    Assert.assertEquals(vo.getPkiAuf(), "D052");
                    break;
                default:
                    throw new TestException("Unexpected entry in rufnummernProtierung found!");
            }
        }
    }

    @Test
    public void compareNumbersAndDefineStatusInfoCheckAngefragt() throws Exception {

        RufnummernportierungEinzeln rufnummerRequest = new RufnummernportierungEinzelnTestBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("1").build())
                .addRufnummer(new RufnummerOnkzTestBuilder().withOnkz("821").withRufnummer("2").build())
                .build();
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> wbciRequest = new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallKueMrn>()
                .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnBuilder()
                        .withRufnummernportierung(rufnummerRequest)
                        .build())
                .build();

        RufnummernportierungVOBuilder cut = new RufnummernportierungVOBuilder();
        List<RufnummernportierungVO> result = cut.withWbciRequest(wbciRequest).build();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        for (RufnummernportierungVO vo : result) {
            switch (vo.getDnBase()) {
                case "1":
                    Assert.assertEquals(vo.getStatusInfo(), RufnummernportierungVO.StatusInfo.ANGEFRAGT);
                    break;
                case "2":
                    Assert.assertEquals(vo.getStatusInfo(), RufnummernportierungVO.StatusInfo.ANGEFRAGT);
                    break;
                default:
                    throw new TestException("Unexpected entry in rufnummernProtierung found!");
            }
        }
    }

}
