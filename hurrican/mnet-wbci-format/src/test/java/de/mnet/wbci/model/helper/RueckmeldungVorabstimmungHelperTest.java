/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.13
 */
package de.mnet.wbci.model.helper;

import static de.mnet.wbci.TestGroups.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.StrasseTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class RueckmeldungVorabstimmungHelperTest {
    @Test
    public void testIsLineIdPresent() throws Exception {
        Assert.assertTrue(RueckmeldungVorabstimmungHelper.isLineIdPresent(createRuemVAwithLineId(1), "DTAG.00000"));
        Assert.assertTrue(RueckmeldungVorabstimmungHelper.isLineIdPresent(createRuemVAwithLineId(5), "DTAG.00001"));
        Assert.assertFalse(RueckmeldungVorabstimmungHelper.isLineIdPresent(createRuemVAwithLineId(5), "DTAG.XXXXX"));
        Assert.assertFalse(RueckmeldungVorabstimmungHelper.isLineIdPresent(createRuemVAwithLineId(0), "DTAG.XXXXX"));
    }

    @Test
    public void testIsWitaVertragsnummerPresent() throws Exception {
        Assert.assertTrue(RueckmeldungVorabstimmungHelper.isWitaVertragsnummerPresent(createRuemVAwithWitaVertNr(1),
                "V00000"));
        Assert.assertTrue(RueckmeldungVorabstimmungHelper.isWitaVertragsnummerPresent(createRuemVAwithWitaVertNr(5),
                "V00002"));
        Assert.assertFalse(RueckmeldungVorabstimmungHelper.isWitaVertragsnummerPresent(createRuemVAwithWitaVertNr(5),
                "VXXXXX"));
        Assert.assertFalse(RueckmeldungVorabstimmungHelper.isWitaVertragsnummerPresent(createRuemVAwithWitaVertNr(0),
                "VXXXXX"));
    }

    @Test
    public void testExtractWitaVtrNrsAndLineIds() throws Exception {
        String result = RueckmeldungVorabstimmungHelper.extractWitaVtrNrsAndLineIds(createRuemVAwithWitaVertNr(0));
        Assert.assertTrue(result.isEmpty());

        result = RueckmeldungVorabstimmungHelper.extractWitaVtrNrsAndLineIds(createRuemVAwithWitaVertNr(2));
        Assert.assertTrue(result.startsWith("WITA Vertragsnummern:"));
        Assert.assertFalse(result.contains("WBCI Line-IDs"));
        Assert.assertTrue(result.contains("V00000"));
        Assert.assertTrue(result.contains("V00001"));

        result = RueckmeldungVorabstimmungHelper.extractWitaVtrNrsAndLineIds(createRuemVAwithLineId(2));
        Assert.assertTrue(result.startsWith("WBCI Line-IDs"));
        Assert.assertFalse(result.contains("WITA Vertragsnummern:"));
        Assert.assertTrue(result.contains("DTAG.00000"));
        Assert.assertTrue(result.contains("DTAG.00001"));
    }

    @Test
    public void testGetWitaVtrNrs() throws Exception {
        List<String> result = RueckmeldungVorabstimmungHelper.getWitaVtrNrs(createRuemVAwithLineId(0));
        Assert.assertTrue(result.isEmpty());

        result = RueckmeldungVorabstimmungHelper.getWitaVtrNrs(createRuemVAwithWitaVertNr(2));
        Assert.assertTrue(result.contains("V00000"));
        Assert.assertTrue(result.contains("V00001"));

        result = RueckmeldungVorabstimmungHelper.getWitaVtrNrs(createRuemVAwithLineId(2));
        Assert.assertFalse(result.contains("DTAG.00000"));
        Assert.assertFalse(result.contains("DTAG.00001"));
    }

    @Test
    public void testGetWitaVtrNrsAndLineIds() throws Exception {
        List<String> result = RueckmeldungVorabstimmungHelper.getWitaVtrNrsAndLineIds(createRuemVAwithLineId(0));
        Assert.assertTrue(result.isEmpty());

        result = RueckmeldungVorabstimmungHelper.getWitaVtrNrsAndLineIds(createRuemVAwithWitaVertNr(2));
        Assert.assertTrue(result.contains("V00000"));
        Assert.assertTrue(result.contains("V00001"));

        result = RueckmeldungVorabstimmungHelper.getWitaVtrNrsAndLineIds(createRuemVAwithLineId(2));
        Assert.assertTrue(result.contains("DTAG.00000"));
        Assert.assertTrue(result.contains("DTAG.00001"));
    }


    @Test
    public void testExtractAdaInfos() {
        Set<MeldungPositionRueckmeldungVa> meldungPosSet = new HashSet<>();
        meldungPosSet.add(new MeldungPositionRueckmeldungVaTestBuilder()
                .withMeldungsCode(MeldungsCode.ADAPLZ)
                .withStandortAbweichend(new StandortTestBuilder().withPostleitzahl("88888").build())
                .build());
        meldungPosSet.add(new MeldungPositionRueckmeldungVaTestBuilder()
                .withMeldungsCode(MeldungsCode.ADAORT)
                .withStandortAbweichend(new StandortTestBuilder().withOrt("Ort").build())
                .build());
        meldungPosSet.add(new MeldungPositionRueckmeldungVaTestBuilder()
                .withMeldungsCode(MeldungsCode.ADASTR)
                .withStandortAbweichend(new StandortTestBuilder()
                        .withStrasse(new StrasseTestBuilder().withStrassenname("Strasse").build())
                        .build())
                .build());
        meldungPosSet.add(new MeldungPositionRueckmeldungVaTestBuilder()
                .withMeldungsCode(MeldungsCode.ADAHSNR)
                .withStandortAbweichend(new StandortTestBuilder()
                        .withStrasse(new StrasseTestBuilder().withHausnummer("11").withHausnummernZusatz("b").build())
                        .build())
                .build());

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withMeldungsPositionen(meldungPosSet)
                .build();

        String result = RueckmeldungVorabstimmungHelper.extractAdaInfos(ruemVa);
        Assert.assertTrue(result.contains("ADASTR: Strasse"));
        Assert.assertTrue(result.contains("ADAORT: Ort"));
        Assert.assertTrue(result.contains("ADAPLZ: 88888"));
        Assert.assertTrue(result.contains("ADAHSNR: 11 b"));
    }


    private RueckmeldungVorabstimmung createRuemVAwithLineId(int countOfLines) {
        RueckmeldungVorabstimmungTestBuilder ruemVaBuilder = new RueckmeldungVorabstimmungTestBuilder();
        for (int i = 0; i < countOfLines; i++) {
            ruemVaBuilder.addTechnischeRessource(
                    new TechnischeRessourceTestBuilder()
                            .withLineId(CarrierCode.DTAG + ".0000" + i)
                            .build()
            );
        }
        RueckmeldungVorabstimmung ruemVa = ruemVaBuilder.buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        if (countOfLines == 0) {
            ruemVa.setTechnischeRessourcen(null);
        }
        return ruemVa;
    }

    private RueckmeldungVorabstimmung createRuemVAwithWitaVertNr(int countOfVtrNr) {
        RueckmeldungVorabstimmungTestBuilder ruemVaBuilder = new RueckmeldungVorabstimmungTestBuilder();
        for (int i = 0; i < countOfVtrNr; i++) {
            ruemVaBuilder.addTechnischeRessource(
                    new TechnischeRessourceTestBuilder()
                            .withVertragsnummer("V0000" + i)
                            .build()
            );
        }
        RueckmeldungVorabstimmung ruemVa = ruemVaBuilder.buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        if (countOfVtrNr == 0) {
            ruemVa.setTechnischeRessourcen(null);
        }
        return ruemVa;
    }

}
