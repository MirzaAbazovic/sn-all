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
public class InboundMessageCommentGeneratorTest {
    private InboundMessageCommentGenerator testling = new InboundMessageCommentGenerator();
    private LocalDateTime date = LocalDateTime.of(LocalDate.parse("2013-01-01", DateTimeFormatter.ISO_LOCAL_DATE), LocalTime.MIN);
    private String vorabstimmungsId = "12345678";
    private String aenderungsId = "23456789";
    private String stornoId = "34567890";

    @Test
    public void testGetVaBemerkung() throws Exception {
        String expected = "Kündigung mit Rufnummernportierung von 'DTAG' zum Kundenwunschtermin 01.01.2013 erhalten. (VA-ID '12345678')";
        Assert.assertEquals(testling.getVaBemerkung(GeschaeftsfallTyp.VA_KUE_MRN, CarrierCode.DTAG, date, vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvBemerkung() throws Exception {
        String expected = "Terminverschiebung von 'DTAG' zum neuen Kundenwunschtermin '01.01.2013' erhalten. (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvBemerkung(CarrierCode.DTAG, date, aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAufhAbgBemerkung() throws Exception {
        String expected = "Stornierung Aufhebungsanfrage von 'DTAG' erhalten. Stornogrund: 'some reason' (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAufhAbgBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAufhAufBemerkung() throws Exception {
        String expected = "Stornierung Aufhebungsanfrage von 'DTAG' erhalten. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAufhAufBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAenAbgBemerkung() throws Exception {
        String expected = "Stornierung Änderungsanfrage von 'DTAG' erhalten. Stornogrund: 'some reason' (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAenAbgBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStrAenAufBemerkung() throws Exception {
        String expected = "Stornierung Änderungsanfrage von 'DTAG' erhalten. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStrAenAufBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Vorabstimmung von 'DTAG' erhalten. Grund: some reason (VA-ID '12345678')";
        Assert.assertEquals(testling.getAbbmBemerkung(CarrierCode.DTAG, "some reason", vorabstimmungsId), expected);
    }

    @Test
    public void testGetRuemVaBemerkung() throws Exception {
        String expected = "Vorabstimmungsantwort von 'DTAG' erhalten. Bestätigter Wechseltermin: 01.01.2013 (VA-ID '12345678')";
        Assert.assertEquals(testling.getRuemVaBemerkung(CarrierCode.DTAG, date, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAkmTrBemerkung() throws Exception {
        String expected = "Meldung zur Übernahme der techn. Ressource von 'DTAG' erhalten. Übernahme gewünscht: Ja. (VA-ID '12345678')";
        Assert.assertEquals(testling.getAkmTrBemerkung(CarrierCode.DTAG, Boolean.TRUE, vorabstimmungsId), expected);
    }

    @Test
    public void testGetAbbmTrBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Übernahme der techn. Ressource von 'DTAG' erhalten. Grund: some reason (VA-ID '12345678')";
        Assert.assertEquals(testling.getAbbmTrBemerkung(CarrierCode.DTAG, "some reason", vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvErlmBemerkung() throws Exception {
        String expected = "Erledigungsmeldung zur Terminverschiebung von 'DTAG' erhalten. Bestätigter Wechseltermin: 01.01.2013 (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvErlmBemerkung(CarrierCode.DTAG, date, aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetTvAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Terminverschiebung von 'DTAG' erhalten. Grund: some reason (TV-ID '23456789', VA-ID '12345678')";
        Assert.assertEquals(testling.getTvAbbmBemerkung(CarrierCode.DTAG, "some reason", aenderungsId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStornoErlmBemerkung() throws Exception {
        String expected = "Erledigungsmeldung zur Stornoanfrage von 'DTAG' erhalten. (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStornoErlmBemerkung(CarrierCode.DTAG, stornoId, vorabstimmungsId), expected);
    }

    @Test
    public void testGetStornoAbbmBemerkung() throws Exception {
        String expected = "Abbruchmeldung zur Stornoanfrage von 'DTAG' erhalten. Grund: some reason (StornoID '34567890', VA-ID '12345678')";
        Assert.assertEquals(testling.getStornoAbbmBemerkung(CarrierCode.DTAG, "some reason", stornoId, vorabstimmungsId), expected);
    }
}
