package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangAutomationError;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;

@Test(groups = { BaseTest.SLOW })
public class CarrierElTALServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CarrierElTALServiceTest.class);

    @Autowired
    CarrierElTALService carrierElTALService;
    @Autowired
    CarrierService carrierService;
    @Autowired
    EndstellenService endstellenService;

    @Test
    public void findCBVorgaenge4CB() throws Exception {
        CBVorgang cbVorgang1 = getBuilder(CBVorgangBuilder.class).setPersist(true).get();
        CBVorgang cbVorgang2 = getBuilder(CBVorgangBuilder.class).setPersist(true).get();

        List<CBVorgang> cbVorgaenge = carrierElTALService.findCBVorgaenge4CB(cbVorgang1.getCbId(), cbVorgang2.getCbId());
        Assert.assertEquals(2, cbVorgaenge.size());
        Iterator<CBVorgang> it = cbVorgaenge.iterator();

        List<Long> expectedCbIDs = Arrays.asList(cbVorgang1.getCbId(), cbVorgang2.getCbId());
        Assert.assertTrue(expectedCbIDs.contains(it.next().getCbId()));
        Assert.assertTrue(expectedCbIDs.contains(it.next().getCbId()));
    }

    public void testFindOpenCBVorgaengeNiederlassung() throws Exception {
        CBVorgangBuilder cbVorgangBuilder = getBuilder(CBVorgangBuilder.class);
        cbVorgangBuilder.getAuftragBuilder().withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class));
        cbVorgangBuilder.withStatus(CBVorgang.STATUS_SUBMITTED).withSubmittedAt(new Date()).withAutomation(Boolean.FALSE);
        cbVorgangBuilder.build();

        List<CBVorgangNiederlassung> cbVorgangNiederlassungList = carrierElTALService.findOpenCBVorgaengeNiederlassungWithWiedervorlage();
        LOGGER.debug("testFindOpenCBVorgaengeNiederlassung() - finde " + cbVorgangNiederlassungList.size() + " Vorgänge.");
        String name = cbVorgangBuilder.getAuftragBuilder().getAuftragTechnikBuilder().getNiederlassungBuilder().get().getName();

        int found = 0;
        for (CBVorgangNiederlassung cbVorgangNiederlassung : cbVorgangNiederlassungList) {
            if (name.equals(cbVorgangNiederlassung.getNiederlassung())) {
                found++;
            }
        }
        assertEquals(found, 1, "Niederlassung mit Namen " + name + " muss genau einmal vorkommen");
    }


    public void testFindOpenCBVorgaengeNiederlassungWithWiedervorlage() throws Exception {
        NiederlassungBuilder nlBuilder = getBuilder(NiederlassungBuilder.class);
        AuftragTechnikBuilder atBuilder1 = getBuilder(AuftragTechnikBuilder.class).withNiederlassungBuilder(nlBuilder);
        AuftragTechnikBuilder atBuilder2 = getBuilder(AuftragTechnikBuilder.class).withNiederlassungBuilder(nlBuilder);

        CBVorgangBuilder cbVorgangBuilderWithNiedervorlageInFuture = getBuilder(CBVorgangBuilder.class);
        cbVorgangBuilderWithNiedervorlageInFuture.getAuftragBuilder().withAuftragTechnikBuilder(atBuilder1);
        cbVorgangBuilderWithNiedervorlageInFuture.withStatus(CBVorgang.STATUS_SUBMITTED).withSubmittedAt(new Date())
                .withWiedervorlageAm(LocalDateTime.now().plusMinutes(2)).withAutomation(Boolean.FALSE);
        cbVorgangBuilderWithNiedervorlageInFuture.build();

        CBVorgangBuilder cbVorgangBuilderWithNiedervorlageNotSet = getBuilder(CBVorgangBuilder.class);
        cbVorgangBuilderWithNiedervorlageNotSet.getAuftragBuilder().withAuftragTechnikBuilder(atBuilder2);
        cbVorgangBuilderWithNiedervorlageNotSet.withStatus(CBVorgang.STATUS_SUBMITTED).withSubmittedAt(new Date()).withAutomation(Boolean.FALSE);
        cbVorgangBuilderWithNiedervorlageNotSet.build();

        List<CBVorgangNiederlassung> cbVorgangNiederlassungList = carrierElTALService.findOpenCBVorgaengeNiederlassungWithWiedervorlage();
        LOGGER.debug("testFindOpenCBVorgaengeNiederlassungWithWiedervorlage(() - finde " + cbVorgangNiederlassungList.size() + " Vorgänge.");
        String name = nlBuilder.get().getName();
        int found = 0;
        for (CBVorgangNiederlassung cbVorgangNiederlassung : cbVorgangNiederlassungList) {
            LOGGER.trace("testFindOpenCBVorgaengeNiederlassungClosedStatus() - niederlassung is "
                    + cbVorgangNiederlassung.getNiederlassung());
            if (name.equals(cbVorgangNiederlassung.getNiederlassung())) {
                found++;
            }
        }
        assertEquals(found, 1, "Niederlassung mit Namen " + name + " muss genau einmal vorkommen");

    }

    public void testFindOpenCBVorgaengeNiederlassungClosedStatus() throws Exception {
        NiederlassungBuilder nlBuilder = getBuilder(NiederlassungBuilder.class);
        AuftragTechnikBuilder atBuilderOpen = getBuilder(AuftragTechnikBuilder.class).withNiederlassungBuilder(
                nlBuilder);
        AuftragTechnikBuilder atBuilderClosed = getBuilder(AuftragTechnikBuilder.class).withNiederlassungBuilder(
                nlBuilder);

        // ein geschlossener, ein offener Vorgang
        CBVorgangBuilder cbVorgangClosedBuilder = getBuilder(CBVorgangBuilder.class).withStatusClosed().withAutomation(Boolean.FALSE);
        cbVorgangClosedBuilder.getAuftragBuilder().withAuftragTechnikBuilder(atBuilderOpen);
        cbVorgangClosedBuilder.build();
        CBVorgangBuilder cbVorgangOpenBuilder = getBuilder(CBVorgangBuilder.class).withStatus(
                CBVorgang.STATUS_SUBMITTED).withSubmittedAt(new Date()).withAutomation(Boolean.FALSE);
        cbVorgangOpenBuilder.getAuftragBuilder().withAuftragTechnikBuilder(atBuilderClosed);
        cbVorgangOpenBuilder.build();

        List<CBVorgangNiederlassung> cbVorgangNiederlassungList = carrierElTALService.findOpenCBVorgaengeNiederlassungWithWiedervorlage();
        LOGGER.debug("testFindOpenCBVorgaengeNiederlassung() - finde " + cbVorgangNiederlassungList.size() + " Vorgänge.");
        String name = nlBuilder.get().getName();
        int found = 0;
        for (CBVorgangNiederlassung cbVorgangNiederlassung : cbVorgangNiederlassungList) {
            LOGGER.debug("testFindOpenCBVorgaengeNiederlassungClosedStatus() - niederlassung is "
                    + cbVorgangNiederlassung.getNiederlassung());
            if (name.equals(cbVorgangNiederlassung.getNiederlassung())) {
                found++;
            }
        }
        assertEquals(found, 1, "Niederlassung mit Namen " + name + " muss genau einmal vorkommen");
    }

    public void testFindMultipleOpenCBVorgaengeNiederlassung() throws Exception {
        NiederlassungBuilder nlBuilder = getBuilder(NiederlassungBuilder.class);
        AuftragTechnikBuilder atBuilderOne = getBuilder(AuftragTechnikBuilder.class)
                .withNiederlassungBuilder(nlBuilder);
        AuftragTechnikBuilder atBuilderTwo = getBuilder(AuftragTechnikBuilder.class)
                .withNiederlassungBuilder(nlBuilder);

        // zwei offene Vorgänge
        CBVorgangBuilder cbVorgangTransferredBuilder = getBuilder(CBVorgangBuilder.class).withStatus(
                CBVorgang.STATUS_TRANSFERRED).withAutomation(Boolean.FALSE);
        cbVorgangTransferredBuilder.getAuftragBuilder().withAuftragTechnikBuilder(atBuilderOne);
        cbVorgangTransferredBuilder.build();
        CBVorgangBuilder cbVorgangSubmittedBuilder = getBuilder(CBVorgangBuilder.class).withStatus(
                CBVorgang.STATUS_SUBMITTED).withSubmittedAt(new Date()).withAutomation(Boolean.FALSE);
        cbVorgangSubmittedBuilder.getAuftragBuilder().withAuftragTechnikBuilder(atBuilderTwo);
        cbVorgangSubmittedBuilder.build();

        List<CBVorgangNiederlassung> cbVorgangNiederlassungList = carrierElTALService.findOpenCBVorgaengeNiederlassungWithWiedervorlage();
        LOGGER.debug("testFindMultipleOpenCBVorgaengeNiederlassung() - finde " + cbVorgangNiederlassungList.size() + " Vorgänge.");

        String name = nlBuilder.get().getName();
        int found = 0;
        for (CBVorgangNiederlassung cbVorgangNiederlassung : cbVorgangNiederlassungList) {
            LOGGER.debug("testFindMultipleOpenCBVorgaengeNiederlassung() - niederlassung is "
                    + cbVorgangNiederlassung.getNiederlassung());
            if (name.equals(cbVorgangNiederlassung.getNiederlassung())) {
                found++;
            }
        }
        assertEquals(found, 2, "Niederlassung mit Namen " + name + " muss genau zweimal vorkommen");
    }

    public void testSaveOfCBVorgangWithLBZWithLeadingZeros() throws Exception {
        NiederlassungBuilder nlBuilder = getBuilder(NiederlassungBuilder.class);
        AuftragTechnikBuilder atBuilderOne = getBuilder(AuftragTechnikBuilder.class)
                .withNiederlassungBuilder(nlBuilder);
        CBVorgangBuilder cbVorgangTransferredBuilder = getBuilder(CBVorgangBuilder.class)
                .withStatus(CBVorgang.STATUS_TRANSFERRED).withSubmittedAt(new Date())
                .withReturnLBZ("0001/0002/0003/0004")
                .withAutomation(Boolean.FALSE);
        cbVorgangTransferredBuilder.getAuftragBuilder().withAuftragTechnikBuilder(atBuilderOne);
        CBVorgang cbVorgang = cbVorgangTransferredBuilder.build();

        CBVorgang lookedUp = carrierElTALService.findCBVorgang(cbVorgang.getId());

        assertEquals(lookedUp.getReturnLBZ(), "0001/0002/0003/0004");
    }

    public void testFindCBVorgangByCarrierRefNrWithNull() throws Exception {
        assertNull(carrierElTALService.findCBVorgangByCarrierRefNr(null));
    }

    public void testFindCBVorgangByCarrierRefNr() throws Exception {
        String carrierRefNr = "TEST789456";
        CBVorgang cbVorgang = getBuilder(CBVorgangBuilder.class).withCarrierRefNr(carrierRefNr).withAutomation(Boolean.FALSE).build();
        flushAndClear();

        CBVorgang result = carrierElTALService.findCBVorgangByCarrierRefNr(carrierRefNr);
        assertEquals(result, cbVorgang);
    }

    public void testCreateCbvAutomationError() throws StoreException {
        CBVorgang cbv = getBuilder(CBVorgangBuilder.class)
                .withStatus(CBVorgang.STATUS_ANSWERED)
                .withReturnOk(Boolean.TRUE)
                .withAutomation(Boolean.TRUE)
                .withTyp(CBVorgang.TYP_KUENDIGUNG)
                .withAnsweredAt(new Date())
                .build();
        cbv.addAutomationError(new RuntimeException("test"));
        carrierElTALService.saveCBVorgang(cbv);
        flushAndClear();

        assertNotEmpty(cbv.getAutomationErrors());

        CBVorgangAutomationError error = cbv.getAutomationErrors().iterator().next();
        assertNotNull(error.getId());
        assertEquals(error.getErrorMessage(), "test");
    }
}
