/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.13
 */
package de.mnet.wbci.model;

import java.time.format.*;
import java.util.*;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.mnet.wbci.converter.RufnummerConverter;
import de.mnet.wbci.exception.InvalidRufnummerPortierungException;
import de.mnet.wbci.model.builder.DecisionVOBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class DecisionVOHelperTest extends BaseTest {

    @Test
    public void testExtractMeldungPositionAbbruchmeldung() {
        DecisionVO decisionADFORT = buildDecisionVO(DecisionAttribute.ORT, MeldungsCode.ADFORT, "ort");
        DecisionVO decisionVORng1 = buildDecisionVO(DecisionAttribute.RUFNUMMER, MeldungsCode.RNG, "0821/123456");
        DecisionVO decisionVORng2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, MeldungsCode.RNG, "0821/123457");

        Set<MeldungPositionAbbmRufnummer> activeAbbmRufnummern = Sets.newHashSet(
                new MeldungPositionAbbmRufnummer("8210001"), new MeldungPositionAbbmRufnummer("8210003"));

        Set<MeldungPositionAbbruchmeldung> result = DecisionVOHelper.extractMeldungPositionAbbruchmeldung(
                Arrays.asList(decisionADFORT, decisionVORng1, decisionVORng2), activeAbbmRufnummern);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);

        for (MeldungPositionAbbruchmeldung abbmPos : result) {
            if (MeldungsCode.RNG.equals(abbmPos.getMeldungsCode())) {
                Set<MeldungPositionAbbmRufnummer> abbmPosRufnummer = abbmPos.getRufnummern();
                Assert.assertNotNull(abbmPosRufnummer);
                Assert.assertEquals(abbmPosRufnummer.size(), 2);
            }
        }
    }

    @Test
    public void testExtractMeldungPositionRueckmeldungVaCheckAddress() {
        DecisionVO decisionZwa = new DecisionVO(DecisionAttribute.NACHNAME);
        decisionZwa.setFinalMeldungsCode(MeldungsCode.ZWA);
        DecisionVO decisionZwa2 = new DecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN);
        decisionZwa2.setFinalMeldungsCode(MeldungsCode.ZWA);

        Adresse address = new AdresseBuilder().withNummer("99").build();
        DecisionVO decisionCity = buildDecisionVO(DecisionAttribute.ORT, address, MeldungsCode.ADAORT);
        DecisionVO decisionPlz = buildDecisionVO(DecisionAttribute.PLZ, address, MeldungsCode.ADAPLZ);
        DecisionVO decisionStreet = buildDecisionVO(DecisionAttribute.STRASSENNAME, address, MeldungsCode.ADASTR);
        DecisionVO decisionHouseNum = buildDecisionVO(DecisionAttribute.HAUSNUMMER, address, MeldungsCode.ADAHSNR);

        Set<MeldungPositionRueckmeldungVa> result = DecisionVOHelper.extractMeldungPositionRueckmeldungVa(
                Arrays.asList(decisionZwa, decisionZwa2, decisionCity, decisionPlz, decisionStreet, decisionHouseNum));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 5);

        assertStandortAbweichendEquals(MeldungsCode.ADAORT, result, address);
        assertStandortAbweichendEquals(MeldungsCode.ADAPLZ, result, address);
        assertStandortAbweichendEquals(MeldungsCode.ADASTR, result, address);
        assertStandortAbweichendEquals(MeldungsCode.ADAHSNR, result, address);
        int numberZwaFound = 0;
        for (MeldungPositionRueckmeldungVa meldungPositionRueckmeldungVa : result) {
            if (MeldungsCode.ZWA.equals(meldungPositionRueckmeldungVa.getMeldungsCode())) {
                numberZwaFound++;
            }
        }
        Assert.assertEquals(numberZwaFound, 1);
    }

    @Test
    public void testExtractMeldungPositionRueckmeldungVaNat() {
        DecisionVO decisionZwa = new DecisionVO(DecisionAttribute.NACHNAME);
        decisionZwa.setFinalMeldungsCode(MeldungsCode.ZWA);

        DecisionVO decisionKundenwunschtermin = new DecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN);
        decisionKundenwunschtermin.setFinalMeldungsCode(MeldungsCode.NAT);

        Set<MeldungPositionRueckmeldungVa> result = DecisionVOHelper.extractMeldungPositionRueckmeldungVa(
                Arrays.asList(decisionZwa, decisionKundenwunschtermin));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.iterator().next().getMeldungsCode(), MeldungsCode.NAT);
    }

    @Test
    public void testConsolidateZwaMeldungsCodes() {
        DecisionVO decisionZwa = new DecisionVO(DecisionAttribute.NACHNAME);
        decisionZwa.setFinalMeldungsCode(MeldungsCode.ZWA);

        DecisionVO decisionKundenwunschtermin = new DecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN);
        decisionKundenwunschtermin.setFinalMeldungsCode(MeldungsCode.ZWA);
        DecisionVO decisionADA = new DecisionVO(DecisionAttribute.STRASSENNAME);
        decisionADA.setFinalMeldungsCode(MeldungsCode.ADASTR);

        Collection<DecisionVO> result = DecisionVOHelper.consolidateZwaAndNatMeldungsCodes(
                Arrays.asList(decisionZwa, decisionKundenwunschtermin, decisionADA));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        assertContains(result, MeldungsCode.ZWA, MeldungsCode.ADASTR);
    }

    @Test
    public void testConsolidateZwaNatMeldungsCodes() {
        DecisionVO decisionZwa = new DecisionVO(DecisionAttribute.NACHNAME);
        decisionZwa.setFinalMeldungsCode(MeldungsCode.ZWA);

        DecisionVO decisionKundenwunschtermin = new DecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN);
        decisionKundenwunschtermin.setFinalMeldungsCode(MeldungsCode.NAT);
        DecisionVO decisionADA = new DecisionVO(DecisionAttribute.STRASSENNAME);
        decisionADA.setFinalMeldungsCode(MeldungsCode.ADASTR);

        Collection<DecisionVO> result = DecisionVOHelper.consolidateZwaAndNatMeldungsCodes(
                Arrays.asList(decisionZwa, decisionKundenwunschtermin, decisionADA));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        assertContains(result, MeldungsCode.NAT, MeldungsCode.ADASTR);
    }

    @Test
    public void testConsolidateZwaNatZWAMeldungsCodes() {
        DecisionVO decisionZwa = new DecisionVO(DecisionAttribute.NACHNAME);
        decisionZwa.setFinalMeldungsCode(MeldungsCode.ZWA);

        DecisionVO decisionKundenwunschtermin = new DecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN);
        decisionKundenwunschtermin.setFinalMeldungsCode(MeldungsCode.NAT);
        DecisionVO decisionADA = new DecisionVO(DecisionAttribute.STRASSENNAME);
        decisionADA.setFinalMeldungsCode(MeldungsCode.ZWA);

        Collection<DecisionVO> result = DecisionVOHelper.consolidateZwaAndNatMeldungsCodes(
                Arrays.asList(decisionZwa, decisionKundenwunschtermin, decisionADA));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        assertContains(result, MeldungsCode.NAT);
    }

    @DataProvider(name = "updateFinalResultData")
    public Object[][] updateFinalResultData() {
        // ruemvaDays, vaDays, valid
        return new Object[][] {
                { DecisionResult.NICHT_OK, "Max", "Moritz", DecisionAttribute.VORNAME, MeldungsCode.AIFVN, "Max" },
                { DecisionResult.ABWEICHEND, "Max", "Moritz", DecisionAttribute.VORNAME, MeldungsCode.AIFVN, "Max" },
                { DecisionResult.OK, "Max", "Moritz", DecisionAttribute.VORNAME, MeldungsCode.ZWA, "Moritz" },
                { DecisionResult.MANUELL, "Max", "Moritz", DecisionAttribute.VORNAME, null, null },
                { DecisionResult.INFO, "Max", "Moritz", DecisionAttribute.VORNAME, null, null },
                { DecisionResult.NICHT_OK, "123", "133", DecisionAttribute.RUFNUMMER, MeldungsCode.RNG, "133" },
        };
    }

    @Test(dataProvider = "updateFinalResultData")
    public void testUpdateFinalResult(DecisionResult decisionResult, String controlValue, String propertyValue, DecisionAttribute attribute, MeldungsCode finalMeldungsCode, String expectedFinalValue) throws Exception {
        DecisionVO vo = buildDecisionVO(attribute, finalMeldungsCode, propertyValue);
        vo.setControlValue(controlValue);
        DecisionVOHelper.updateFinalResult(vo, decisionResult);
        Assert.assertEquals(vo.getFinalResult(), decisionResult);
        Assert.assertEquals(vo.getFinalValue(), expectedFinalValue);
        Assert.assertEquals(vo.getFinalMeldungsCode(), finalMeldungsCode);
    }

    @Test
    public void testIsAlleRufnummernPortieren() throws Exception {
        DecisionVO voAlleRufnummern = buildDecisionVO(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, MeldungsCode.ZWA, "");
        DecisionVO voOrt = buildDecisionVO(DecisionAttribute.ORT, MeldungsCode.ADAORT, "TEST");

        Assert.assertFalse(DecisionVOHelper.isAlleRufnummernPortieren(Collections.<DecisionVO>emptyList()));
        Assert.assertFalse(DecisionVOHelper.isAlleRufnummernPortieren(Arrays.asList(voOrt)));
        Assert.assertTrue(DecisionVOHelper.isAlleRufnummernPortieren(Arrays.asList(voOrt, voAlleRufnummern)));
    }

    @Test
    public void testFindKundenwunschterminVo() throws Exception {
        DecisionVO voAlleRufnummern = buildDecisionVO(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, MeldungsCode.ZWA, "");
        DecisionVO voTermin = buildDecisionVO(DecisionAttribute.KUNDENWUNSCHTERMIN, MeldungsCode.NAT, "TEST");

        Assert.assertNull(DecisionVOHelper.findKundenwunschterminVo(Collections.<DecisionVO>emptyList()));
        Assert.assertNull(DecisionVOHelper.findKundenwunschterminVo(Arrays.asList(voAlleRufnummern)));
        Assert.assertEquals(DecisionVOHelper.findKundenwunschterminVo(Arrays.asList(voAlleRufnummern, voTermin)),
                voTermin);
    }

    @Test
    public void testExtractMeldungPositionAbbmRufnummernEinzel() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10031")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr2, DecisionResult.OK, MeldungsCode.RNG);

        Rufnummer rnr3 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10032")
                .build();
        DecisionVO rnrVo3 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr3, DecisionResult.INFO, MeldungsCode.ZWA);

        DecisionVO voAlleRufnummern = buildDecisionVO(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, MeldungsCode.ZWA, "");

        /**
         * DecisionVOs with RNG + ZWA + ZWA(INFO) => only rnrVo should be considered
         */
        Set<MeldungPositionAbbmRufnummer> result = DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(
                rnrVo, rnrVo2, rnrVo3));
        Assert.assertEquals(result.size(), 2);
        assertMeldungPositionAbbmRufnummer(result, rnr);
        assertMeldungPositionAbbmRufnummer(result, rnr3);

        /**
         * DecisionVOs with RNG + ZWA + ZWA(INFO) + ALLE_RUFNUMMERN => rnrVo and rnrVo2 should be considered
         */
        result = DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(rnrVo, rnrVo2, rnrVo3,
                voAlleRufnummern));
        Assert.assertEquals(result.size(), 3);
        assertMeldungPositionAbbmRufnummer(result, rnr);
        assertMeldungPositionAbbmRufnummer(result, rnr2);
        assertMeldungPositionAbbmRufnummer(result, rnr3);

        /**
         * DecisionVOs with ZWA + ZWA + ZWA(INFO) => rnrVo and rnrVo2 should be considered
         */
        rnrVo2.setFinalMeldungsCode(MeldungsCode.ZWA);
        result = DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(rnrVo, rnrVo2, rnrVo3));
        Assert.assertEquals(result.size(), 3);
        assertMeldungPositionAbbmRufnummer(result, rnr);
        assertMeldungPositionAbbmRufnummer(result, rnr2);
        assertMeldungPositionAbbmRufnummer(result, rnr3);

        /**
         * DecisionVOs with RNG + RNG + ZWA(INFO) => rnrVo and rnrVo2 should be NOT considered
         */
        rnrVo.setFinalMeldungsCode(MeldungsCode.RNG);
        rnrVo.setFinalResult(DecisionResult.NICHT_OK);
        rnrVo2.setFinalMeldungsCode(MeldungsCode.RNG);
        rnrVo2.setFinalResult(DecisionResult.NICHT_OK);
        result = DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(rnrVo, rnrVo2, rnrVo3));
        assertEmpty(result);
    }

    @Test
    public void testExtractMeldungPositionAbbmRufnummernBlock() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .withDirectDial("10")
                .withRangeFrom("10")
                .withRangeTo("20")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMERN_BLOCK, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .withDirectDial("30")
                .withRangeFrom("30")
                .withRangeTo("50")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMERN_BLOCK, rnr2, DecisionResult.OK,
                MeldungsCode.RNG);

        /**
         * DecisionVOs with RNG + ZWA => list should be empty
         */
        assertEmpty(DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(
                rnrVo, rnrVo2)));

        /**
         * DecisionVOs with ZWA + ZWA => list should be empty
         */
        rnrVo2.setFinalMeldungsCode(MeldungsCode.ZWA);
        assertEmpty(DecisionVOHelper.extractMeldungPositionAbbmRufnummern(Arrays.asList(
                rnrVo, rnrVo2)));
    }

    @Test
    public void testExtractRufnummerPortierungEinzel() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10031")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr2, DecisionResult.OK, MeldungsCode.RNG);

        DecisionVO voAlleRufnummern = buildDecisionVO(DecisionAttribute.ALLE_RUFNUMMERN_PORTIEREN, MeldungsCode.ZWA, "");

        /**
         * DecisionVOs with RNG + ZWA => only rnrVo should be considered
         */
        Collection<RufnummernportierungVO> result =
                DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(rnrVo, rnrVo2));
        Assert.assertEquals(result.size(), 1);
        assertRufnummernportierungVo(result, rnr);

        /**
         * DecisionVOs with RNG + ZWA + ALLE_RUFNUMMERN => rnrVo and rnrVo2 should be considered
         */
        result = DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(rnrVo, rnrVo2, voAlleRufnummern));
        Assert.assertEquals(result.size(), 2);
        assertRufnummernportierungVo(result, rnr);
        assertRufnummernportierungVo(result, rnr2);

        /**
         * DecisionVOs with ZWA + ZWA => rnrVo and rnrVo2 should be considered
         */
        rnrVo2.setFinalMeldungsCode(MeldungsCode.ZWA);
        result = DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(rnrVo, rnrVo2));
        Assert.assertEquals(result.size(), 2);
        assertRufnummernportierungVo(result, rnr);
        assertRufnummernportierungVo(result, rnr2);

        /**
         * DecisionVOs with ZWA + INFO => only rnrVo should be considered
         */
        rnrVo2.setSuggestedResult(DecisionResult.INFO);
        result = DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(rnrVo, rnrVo2));
        Assert.assertEquals(result.size(), 1);
        assertRufnummernportierungVo(result, rnr);
    }

    @Test
    public void testExtractRufnummerPortierungBlock() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .withDirectDial("10")
                .withRangeFrom("10")
                .withRangeTo("20")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMERN_BLOCK, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .withDirectDial("30")
                .withRangeFrom("30")
                .withRangeTo("50")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMERN_BLOCK, rnr2, DecisionResult.OK, MeldungsCode.RNG);

        /**
         * DecisionVOs with RNG + ZWA => only rnrVo should be considered
         */
        Collection<RufnummernportierungVO> result = DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(
                rnrVo, rnrVo2));
        Assert.assertEquals(result.size(), 1);
        assertRufnummernportierungVo(result, rnr);

        /**
         * DecisionVOs with ZWA + ZWA => rnrVo and rnrVo2 should be considered
         */
        rnrVo2.setFinalMeldungsCode(MeldungsCode.ZWA);
        result = DecisionVOHelper.extractActiveRufnummernportierung(Arrays.asList(
                rnrVo, rnrVo2));
        Assert.assertEquals(result.size(), 2);
        assertRufnummernportierungVo(result, rnr);
        assertRufnummernportierungVo(result, rnr2);
    }

    private MeldungPositionRueckmeldungVa getMeldungPositionRueckmeldungVa(MeldungsCode code,
            Set<MeldungPositionRueckmeldungVa> positionen) {
        for (MeldungPositionRueckmeldungVa pos : positionen) {
            if (code.equals(pos.getMeldungsCode())) {
                return pos;
            }
        }
        return null;
    }

    @Test
    public void testCreateAbbmTvDecisionVo() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        tv.getWbciGeschaeftsfall().setWechseltermin(tv.getWbciGeschaeftsfall().getKundenwunschtermin());

        String controlValue = tv.getTvTermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR));
        String propertyValue = tv.getWbciGeschaeftsfall().getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR));

        List<DecisionVO> abbmTvDecisionVo = DecisionVOHelper.createAbbmTvDecisionVo(tv);
        Assert.assertEquals(abbmTvDecisionVo.size(), 1L);
        Assert.assertEquals(abbmTvDecisionVo.get(0).getAttribute(), DecisionAttribute.KUNDENWUNSCHTERMIN);
        Assert.assertEquals(abbmTvDecisionVo.get(0).getFinalMeldungsCode(), MeldungsCode.TV_ABG);
        Assert.assertEquals(abbmTvDecisionVo.get(0).getFinalResult(), DecisionResult.NICHT_OK);
        Assert.assertEquals(abbmTvDecisionVo.get(0).getPropertyValue(), propertyValue);
        Assert.assertEquals(abbmTvDecisionVo.get(0).getControlValue(), controlValue);
    }

    @Test
    public void testCreateAbbmStornoDecisionVo() throws Exception {
        List<DecisionVO> abbmStornoDecisionVo = DecisionVOHelper.createAbbmStornoDecisionVo();
        Assert.assertEquals(abbmStornoDecisionVo.size(), 1L);
        Assert.assertNull(abbmStornoDecisionVo.get(0).getAttribute());
        Assert.assertEquals(abbmStornoDecisionVo.get(0).getFinalMeldungsCode(), MeldungsCode.STORNO_ABG);
        Assert.assertEquals(abbmStornoDecisionVo.get(0).getFinalResult(), DecisionResult.NICHT_OK);
        Assert.assertNull(abbmStornoDecisionVo.get(0).getPropertyValue());
        Assert.assertNull(abbmStornoDecisionVo.get(0).getControlValue());
    }

    @Test
    public void testCreateAbbmBuilderFromDecisionVo() throws Exception {
        Abbruchmeldung result = DecisionVOHelper.createAbbmBuilderFromDecisionVo(
                Arrays.asList(buildDecisionVO(DecisionAttribute.ORT, MeldungsCode.ADFORT, "München")), "reason",
                new WbciGeschaeftsfallKueMrn(), new HashSet<MeldungPositionAbbmRufnummer>()).build();

        Assert.assertEquals(result.getIoType(), IOType.OUT);
        Assert.assertEquals(result.getAbsender(), CarrierCode.MNET);
        Assert.assertEquals(result.getBegruendung(), "reason");
        Assert.assertTrue(result.getMeldungsCodes().contains("ADFORT"));
        Assert.assertTrue(result.getMeldungsCodes().contains("SONST"));
    }

    @Test
    public void testCreateAbbmBuilderFromDecisionVoEmptyReson() throws Exception {
        Abbruchmeldung result = DecisionVOHelper.createAbbmBuilderFromDecisionVo(
                Arrays.asList(buildDecisionVO(DecisionAttribute.ORT, MeldungsCode.ADFORT, "München")), "    ",
                new WbciGeschaeftsfallKueMrn(), new HashSet<MeldungPositionAbbmRufnummer>()).build();

        Assert.assertEquals(result.getIoType(), IOType.OUT);
        Assert.assertEquals(result.getAbsender(), CarrierCode.MNET);
        Assert.assertEquals(result.getBegruendung(), null);
        Assert.assertTrue(result.getMeldungsCodes().contains("ADFORT"));
        Assert.assertFalse(result.getMeldungsCodes().contains("SONST"));
    }

    @Test
    public void testCreateAbbmTrDecisionVo() throws Exception {
        List<DecisionVO> voList = DecisionVOHelper.createAbbmTrDecisionVo();
        Assert.assertEquals(voList.size(), 1);
        assertContains(voList, MeldungsCode.UETN_NM);
    }

    @Test
    public void testExtractMeldungPositionAbbmTr() throws Exception {
        Set<MeldungPositionAbbruchmeldungTechnRessource> result = DecisionVOHelper
                .extractMeldungPositionAbbmTr(DecisionVOHelper.createAbbmTrDecisionVo());
        Assert.assertEquals(result.size(), 1);
        MeldungPositionAbbruchmeldungTechnRessource pos = result.iterator().next();
        Assert.assertEquals(pos.getPositionTyp(), MeldungPositionTyp.ABBM_TR);
        Assert.assertEquals(pos.getMeldungsCode(), MeldungsCode.UETN_NM);
        Assert.assertEquals(pos.getMeldungsText(), MeldungsCode.UETN_NM.getStandardText());
    }

    @Test
    public void testUpdateRufnummernProstierungSelection() throws Exception {

        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        //This DecisionVO should be ignored all the time
        DecisionVO rnrVoIgnoreThis = buildDecisionVO(DecisionAttribute.RUFNUMMER, new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10040")
                .build(), DecisionResult.OK, MeldungsCode.ZWA);
        rnrVoIgnoreThis.setFinalResult(DecisionResult.INFO);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr2, DecisionResult.OK, MeldungsCode.ZWA);

        /*
         * rnr => rnrVo      ==> OK
         * rnr2 => rnrVo2    ==> OK
         */
        Collection<RufnummerPortierungSelection> result = DecisionVOHelper.updateRufnummerPortierungSelection(
                Arrays.asList(new RufnummerPortierungSelection(rnr), new RufnummerPortierungSelection(rnr2)),
                Arrays.asList(rnrVo, rnrVo2, rnrVoIgnoreThis),
                "DEU.MNET.001");
        assertRufnummerPortierungSelection(result, rnr, true, true);
        assertRufnummerPortierungSelection(result, rnr2, true, true);

        /*
         * rnr => rnrVo            ==> OK
         * rnr2 => no VA Object    ==> OK
         */
        result = DecisionVOHelper.updateRufnummerPortierungSelection(
                Arrays.asList(new RufnummerPortierungSelection(rnr), new RufnummerPortierungSelection(rnr2)),
                Arrays.asList(rnrVo, rnrVoIgnoreThis),
                "DEU.MNET.001");
        assertRufnummerPortierungSelection(result, rnr, true, true);
        assertRufnummerPortierungSelection(result, rnr2, true, false);
    }

    @Test(expectedExceptions = InvalidRufnummerPortierungException.class,
            expectedExceptionsMessageRegExp = "Die Rufnummernportierung der WBCI-Vorabstimmung 'DEU.MNET.001' kann nicht als Selektion für den WITA-Vorgang übernommen werden; die Rufnummern des Taifun-Auftrags sind entsprechend anzupassen!")
    public void testExceptionEmptyRufnummerSelecetionOnUpdateRufnummernProstierungSelection() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        DecisionVOHelper.updateRufnummerPortierungSelection(
                Collections.<RufnummerPortierungSelection>emptyList(),
                Arrays.asList(rnrVo),
                "DEU.MNET.001");
    }

    @Test(expectedExceptions = InvalidRufnummerPortierungException.class,
            expectedExceptionsMessageRegExp = "Die Rufnummernportierung der WBCI-Vorabstimmung 'DEU.MNET.001' kann nicht als Selektion für den WITA-Vorgang übernommen werden; die Rufnummern des Taifun-Auftrags sind entsprechend anzupassen!")
    public void testExceptionEmptyRufnummerOnUpdateRufnummernProstierungSelection() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();

        DecisionVOHelper.updateRufnummerPortierungSelection(
                Arrays.asList(new RufnummerPortierungSelection(rnr)),
                Collections.<DecisionVO>emptyList(),
                "DEU.MNET.001");
    }

    @Test(expectedExceptions = InvalidRufnummerPortierungException.class,
            expectedExceptionsMessageRegExp = "Die Rufnummernportierung der WBCI-Vorabstimmung 'DEU.MNET.001' kann nicht als Selektion für den WITA-Vorgang übernommen werden; die Rufnummern des Taifun-Auftrags sind entsprechend anzupassen!")
    public void testExceptionMissingRufnummerPortSelectionOnUpdateRufnummernProstierungSelection() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr2, DecisionResult.OK, MeldungsCode.ZWA);

        /*
         * rnr => rnrVo                  ==> OK
         * no rnr Object => rnrVo2       ==> ERROR, not all Numbers included
         */
        DecisionVOHelper.updateRufnummerPortierungSelection(
                Arrays.asList(new RufnummerPortierungSelection(rnr)),
                Arrays.asList(rnrVo, rnrVo2),
                "DEU.MNET.001");
    }

    @Test(expectedExceptions = InvalidRufnummerPortierungException.class,
            expectedExceptionsMessageRegExp = "Die Rufnummernportierung der WBCI-Vorabstimmung 'DEU.MNET.001' kann nicht als Selektion für den WITA-Vorgang übernommen werden; die Rufnummern des Taifun-Auftrags sind entsprechend anzupassen!")
    public void testExceptionNoOkDecisionVoOnUpdateRufnummernProstierungSelection() throws Exception {
        Rufnummer rnr = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("10030")
                .build();
        DecisionVO rnrVo = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr, DecisionResult.OK, MeldungsCode.ZWA);

        Rufnummer rnr2 = new RufnummerBuilder()
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withOnKz("89")
                .withDnBase("20030")
                .build();
        DecisionVO rnrVo2 = buildDecisionVO(DecisionAttribute.RUFNUMMER, rnr2, DecisionResult.OK, MeldungsCode.RNG);
        rnrVo2.setFinalResult(DecisionResult.NICHT_OK);

        /*
         * rnr => rnrVo               ==> OK
         * rnr2 => rnrVo2 (RNG)       ==> RNR is nicht in Portierungselection enthalten
         */
        DecisionVOHelper.updateRufnummerPortierungSelection(
                Arrays.asList(new RufnummerPortierungSelection(rnr), new RufnummerPortierungSelection(rnr2)),
                Arrays.asList(rnrVo, rnrVo2),
                "DEU.MNET.001");
    }

    private void assertRufnummerPortierungSelection(Collection<RufnummerPortierungSelection> result, Rufnummer rnr,
            boolean rnrIncluded, boolean selected) {
        Iterator<RufnummerPortierungSelection> it = result.iterator();
        boolean found = false;
        Boolean foundObjectSelected = null;
        while (it.hasNext() && !found) {
            RufnummerPortierungSelection next = it.next();
            if (next.getRufnummer().isRufnummerEqual(rnr)) {
                found = true;
                foundObjectSelected = next.getSelected();
            }
        }
        Assert.assertEquals(found, rnrIncluded);
        if (!rnrIncluded) {
            Assert.assertNull(foundObjectSelected);
        }
        else {
            Assert.assertNotNull(foundObjectSelected);
            Assert.assertTrue(foundObjectSelected.equals(selected));
        }
    }

    private void assertStandortAbweichendEquals(MeldungsCode code, Set<MeldungPositionRueckmeldungVa> ruemVaPos,
            Adresse expectedAddress) {
        Standort standort = getMeldungPositionRueckmeldungVa(code, ruemVaPos).getStandortAbweichend();
        switch (code) {
            case ADAORT:
                Assert.assertEquals(standort.getOrt(), expectedAddress.getOrt());
                Assert.assertNull(standort.getStrasse());
                Assert.assertNull(standort.getPostleitzahl());
                break;
            case ADAPLZ:
                Assert.assertEquals(standort.getPostleitzahl(), expectedAddress.getPlzTrimmed());
                Assert.assertNull(standort.getStrasse());
                Assert.assertNull(standort.getOrt());
                break;
            case ADASTR:
                Assert.assertEquals(standort.getStrasse().getStrassenname(), expectedAddress.getStrasse());
                Assert.assertNull(standort.getPostleitzahl());
                Assert.assertNull(standort.getOrt());
                break;
            case ADAHSNR:
                Assert.assertEquals(standort.getStrasse().getHausnummer(), expectedAddress.getNummer());
                Assert.assertNull(standort.getPostleitzahl());
                Assert.assertNull(standort.getOrt());
                break;
            default:
                Assert.fail(String.format("MeldungsCode %s not valid for assertions!", code.name()));
        }
    }

    private void assertContains(Collection<DecisionVO> result, MeldungsCode... codes) {
        List<MeldungsCode> expectedCodes = new ArrayList<>();
        for (DecisionVO vo : result) {
            expectedCodes.add(vo.getFinalMeldungsCode());
        }
        Assert.assertTrue(expectedCodes.containsAll(Arrays.asList(codes)));
    }

    private DecisionVO buildDecisionVO(DecisionAttribute decisionAttribute, Adresse address,
            MeldungsCode finalMeldungsCode) {
        return new DecisionVOBuilder(decisionAttribute)
                .withControlObject(address)
                .withFinalMeldungsCode(finalMeldungsCode)
                .build();
    }

    private DecisionVO buildDecisionVO(DecisionAttribute decisionAttribute, MeldungsCode finalMeldungsCode,
            String propertyValue) {
        return new DecisionVOBuilder(decisionAttribute)
                .withFinalMeldungsCode(finalMeldungsCode)
                .withPropertyValue(propertyValue)
                .build();
    }

    private DecisionVO buildDecisionVO(DecisionAttribute decisionAttribute, Rufnummer rnr, DecisionResult finalResult,
            MeldungsCode meldungsCode) {
        String formatedRufnummer = RufnummerConverter.convertBillingDn(rnr);
        return new DecisionVOBuilder(decisionAttribute)
                .withControlObject(rnr)
                .withControlValue(formatedRufnummer)
                .withFinalResult(finalResult)
                .withPropertyValue(MeldungsCode.ZWA.equals(meldungsCode) ? formatedRufnummer : null)
                .withFinalMeldungsCode(meldungsCode)
                .build();
    }

    private void assertMeldungPositionAbbmRufnummer(Set<MeldungPositionAbbmRufnummer> result, Rufnummer rnr) {
        boolean found = false;
        for (MeldungPositionAbbmRufnummer vo : result) {
            if (vo.getRufnummer() != null
                    && vo.getRufnummer().equals(rnr.getOnKz() + rnr.getDnBase())) {
                found = true;
            }
        }
        Assert.assertEquals(found, true);
    }

    private void assertRufnummernportierungVo(Collection<RufnummernportierungVO> result, Rufnummer rnr) {
        boolean found = false;
        for (RufnummernportierungVO vo : result) {
            if (((vo.getOnkz() == null && rnr.getOnKz() == null)
                    || vo.getOnkz().equals(rnr.getOnKz()))

                    && ((vo.getDnBase() == null && rnr.getDnBase() == null)
                    || vo.getDnBase().equals(rnr.getDnBase()))

                    && ((vo.getDirectDial() == null && rnr.getDirectDial() == null)
                    || vo.getDirectDial().equals(rnr.getDirectDial()))

                    && ((vo.getBlockFrom() == null && rnr.getRangeFrom() == null)
                    || vo.getBlockFrom().equals(rnr.getRangeFrom()))

                    && ((vo.getBlockTo() == null && rnr.getRangeTo() == null)
                    || vo.getBlockTo().equals(rnr.getRangeTo()))

                    && ((vo.getPkiAbg() == null && rnr.getActCarrier() == null)
                    || vo.getPkiAbg() != null && rnr.getActCarrier() != null)

                    ) {

                found = true;
            }
        }
        Assert.assertEquals(found, true);
    }

}
