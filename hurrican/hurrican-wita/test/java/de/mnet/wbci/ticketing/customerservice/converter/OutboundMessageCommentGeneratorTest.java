/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 05.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import java.time.*;
import java.time.format.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;

@Test(groups = BaseTest.UNIT)
public class OutboundMessageCommentGeneratorTest {
    private OutboundMessageCommentGenerator testling = new OutboundMessageCommentGenerator();
    private LocalDateTime date = LocalDateTime.of(LocalDate.parse("2013-01-01", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);
    private String vorabstimmungsId = "12345678";
    private String aenderungsId = "23456789";
    private String stornoId = "34567890";

    @Test
    public void testGetVaBemerkung() throws Exception {
        String expected = "Kündigung mit Rufnummernportierung zum Kundenwunschtermin 01.01.2013 an 'DTAG' gesendet. (VA-ID '12345678')";
        Assert.assertEquals(testling.getVaBemerkung(GeschaeftsfallTyp.VA_KUE_MRN, CarrierCode.DTAG, date, vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvBemerkung() throws Exception {
        String expected = "Terminverschiebung mit neuem Kundenwunschtermin 01.01.2013 an 'DTAG' gesendet. (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvBemerkung(CarrierCode.DTAG, date, aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAufhAbgBemerkung() throws Exception {
        String expected = "Stornierung Aufhebungsanfrage an 'DTAG' gesendet. Stornogrund: 'some reason' (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAufhAbgBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAufhAufBemerkung() throws Exception {
        String expected = "Stornierung Aufhebungsanfrage an 'DTAG' gesendet. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAufhAufBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAenAbgBemerkung() throws Exception {
        String expected = "Stornierung Änderungsanfrage an 'DTAG' gesendet. Stornogrund: 'some reason' (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAenAbgBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAenAufBemerkung() throws Exception {
        String expected = "Stornierung Änderungsanfrage an 'DTAG' gesendet. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAenAufBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Vorabstimmung an 'DTAG' gesendet. Grund: some reason (VA-ID '12345678')";
        Assert.assertEquals(testling.getAbbmBemerkung(CarrierCode.DTAG, "some reason", vorabstimmungsId), expected);
    }

    @Test
    public void testGetRuemVaBemerkung() throws Exception {
        String expected = "Vorabstimmungsantwort an 'DTAG' gesendet. Bestätigter Wechseltermin: 01.01.2013 (VA-ID '12345678')";
        Assert.assertEquals(testling.getRuemVaBemerkung(CarrierCode.DTAG, date, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAkmTrBemerkung() throws Exception {
        String expected = "Meldung zur Übernahme der techn. Ressource gesendet an 'DTAG'. Übernahme gewünscht: Ja. (VA-ID '12345678')";
        Assert.assertEquals(testling.getAkmTrBemerkung(CarrierCode.DTAG, Boolean.TRUE, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAbbmTrBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Übernahme der techn. Ressource an 'DTAG' gesendet. Grund: some reason (VA-ID '12345678')";
        Assert.assertEquals(testling.getAbbmTrBemerkung(CarrierCode.DTAG, "some reason", vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvErlmBemerkung() throws Exception {
        String expected = "Erledigungsmeldung zur Terminverschiebung an 'DTAG' gesendet. Bestätigter Wechseltermin: 01.01.2013 (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvErlmBemerkung(CarrierCode.DTAG, date, aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Terminverschiebung an 'DTAG' gesendet. Grund: some reason (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvAbbmBemerkung(CarrierCode.DTAG, "some reason", aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStornoErlmBemerkung() throws Exception {
        String expected = "Erledigungsmeldung zur Stornoanfrage an 'DTAG' gesendet. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStornoErlmBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStornoAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Stornoanfrage an 'DTAG' gesendet. Grund: some reason (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStornoAbbmBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }

}