/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:51:39
 */
package de.mnet.wita.servicetest.service;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.acceptance.common.AccStandortDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaTalOrderService;

@Test(groups = BaseTest.SERVICE)
public class WitaTalOrderServiceTest extends AbstractServiceTest {

    @Resource(name = "de.mnet.wita.service.WitaTalOrderService")
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private Provider<AcceptanceTestDataBuilder> acceptanceTestDataBuilderProvider;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    private CarrierElTALService carrierElTALService;

    @Autowired
    private Provider<AccStandortDataBuilder> standortDataBuilderProvider;

    @Autowired
    CBVorgangDAO cbVorgangDao;

    @Test
    public void createWithAuftragsKlammer() throws Exception {
        AccStandortDataBuilder standortData = standortDataBuilderProvider.get();
        CreatedData createdData = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withStandortData(standortData)
                .build(getSessionId());
        CreatedData createdData2 = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withStandortData(standortData)
                .withSessionId(getSessionId()).createData();
        flushAndClear();

        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragIds(
                        ImmutableList.of(createdData.auftrag.getAuftragId(), createdData2.auftrag.getAuftragId()))
                .withCbId(createdData.carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(Date.from(createdData.vorgabeMnet.atZone(ZoneId.systemDefault()).toInstant()))
                .withCbVorgangTyp(CBVorgang.TYP_NEU)
                .withUser(createdData.user)
                .withAutomation(Boolean.FALSE);

        List<CBVorgang> result = witaTalOrderService.createCBVorgang(cbvData);
        assertNotEmpty(result);
        WitaCBVorgang createdCBVorgang = (WitaCBVorgang) result.get(0);
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setAuftragsKlammer(createdCBVorgang.getAuftragsKlammer());
        List<WitaCBVorgang> found = carrierElTALService.findCBVorgaengeByExample(example);
        assertThat(createdCBVorgang, isIn(found));
    }

    @Test
    public void createHvtToKvzCbVorgaenge() throws Exception {
        AccStandortDataBuilder standortData = standortDataBuilderProvider.get();
        final LocalDate kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17));
        CreatedData createdDataNeu = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withVorgabeMnet(kwt.atStartOfDay())
                .withStandortData(standortData)
                .build(getSessionId());
        CreatedData createdDataKue = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withVorgabeMnet(kwt.atStartOfDay())
                .withStandortData(standortData)
                .withSessionId(getSessionId())
                .withCarrierbestellungVtrNr("123")
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96C/8433/8433")
                .build(getSessionId());
        flushAndClear();

        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragIds(ImmutableList.of(createdDataNeu.auftrag.getAuftragId()))
                .withCbId(createdDataNeu.carrierbestellung.getId())
                .withAuftragId4HvtToKvz(createdDataKue.auftrag.getAuftragId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(Date.from(createdDataNeu.vorgabeMnet.atZone(ZoneId.systemDefault()).toInstant()))
                .withCbVorgangTyp(CBVorgang.TYP_HVT_KVZ)
                .withUser(createdDataNeu.user)
                .withAutomation(Boolean.FALSE);

        List<CBVorgang> result = witaTalOrderService.createHvtKvzCBVorgaenge(cbvData);
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        final WitaCBVorgang bereitstellung = (WitaCBVorgang) getCbVorgang(result, CBVorgang.TYP_NEU);
        final WitaCBVorgang kuendigung = (WitaCBVorgang) getCbVorgang(result, CBVorgang.TYP_KUENDIGUNG);
        assertNotNull(bereitstellung.getCbVorgangRefId());
        assertEquals(bereitstellung.getCbVorgangRefId(), kuendigung.getId());
        assertNull(kuendigung.getCbVorgangRefId());
        CBVorgang found = carrierElTALService.findCBVorgang(bereitstellung.getCbVorgangRefId());
        assertEquals(found, kuendigung);
    }

    @Test
    public void createKvzCbVorgangAfterStorno() throws Exception {
        AccStandortDataBuilder standortData = standortDataBuilderProvider.get();
        final LocalDate kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17));
        CreatedData createdDataNeu = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withVorgabeMnet(kwt.atStartOfDay())
                .withStandortData(standortData)
                .build(getSessionId());
        CreatedData createdDataKue = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withVorgabeMnet(kwt.atStartOfDay())
                .withStandortData(standortData)
                .withSessionId(getSessionId())
                .withCarrierbestellungVtrNr("123")
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96C/8433/8433")
                .build(getSessionId());
        flushAndClear();

        CbVorgangData cbvDataKue = new CbVorgangData()
                .addAuftragIds(ImmutableList.of(createdDataKue.auftrag.getAuftragId()))
                .withCbId(createdDataKue.carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(Date.from(createdDataKue.vorgabeMnet.atZone(ZoneId.systemDefault()).toInstant()))
                .withCbVorgangTyp(CBVorgang.TYP_KUENDIGUNG)
                .withUser(createdDataKue.user)
                .withAutomation(Boolean.FALSE);

        final List<CBVorgang> cbVorgang = witaTalOrderService.createCBVorgang(cbvDataKue);
        assertNotEmpty(cbVorgang);
        assertEquals(cbVorgang.size(), 1);
        final CBVorgang kuendigung = cbVorgang.get(0);
        kuendigung.setStatus(CBVorgang.STATUS_ANSWERED);
        kuendigung.setAnsweredAt(new Date());
        kuendigung.setReturnOk(true);

        CbVorgangData cbvDataNeu = new CbVorgangData()
                .addAuftragIds(ImmutableList.of(createdDataNeu.auftrag.getAuftragId()))
                .withCbId(createdDataNeu.carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(Date.from(createdDataNeu.vorgabeMnet.atZone(ZoneId.systemDefault()).toInstant()))
                .withAuftragId4HvtToKvz(kuendigung.getAuftragId())
                .withCbVorgangTyp(CBVorgang.TYP_HVT_KVZ)
                .withUser(createdDataNeu.user)
                .withAutomation(Boolean.FALSE);
        List<CBVorgang> result = witaTalOrderService.createHvtKvzCBVorgaenge(cbvDataNeu);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        final WitaCBVorgang bereitstellung = (WitaCBVorgang) getCbVorgang(result, CBVorgang.TYP_NEU);
        assertNotNull(bereitstellung.getCbVorgangRefId());
        assertEquals(bereitstellung.getCbVorgangRefId(), kuendigung.getId());

        CBVorgang found = carrierElTALService.findCBVorgang(bereitstellung.getCbVorgangRefId());
        assertEquals(found, kuendigung);
    }

    private CBVorgang getCbVorgang(List<CBVorgang> vorgaenge, Long cbVorgangTyp) {
        for (CBVorgang cbVorgang : vorgaenge) {
            if (cbVorgang.getTyp().equals(cbVorgangTyp)) {
                return cbVorgang;
            }
        }
        return null;
    }

    public void testGetWitaCBVorgaengeForTVOrStorno() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        WitaCBVorgang cbVorgang1 = getBuilder(WitaCBVorgangBuilder.class).withAuftragBuilder(auftragBuilder).build();
        WitaCBVorgang cbVorgang2 = getBuilder(WitaCBVorgangBuilder.class).withAuftragBuilder(auftragBuilder).build();
        cbVorgang2.setCbId(null);
        cbVorgang2.setTyp(CBVorgang.TYP_REX_MK);

        List<WitaCBVorgang> cbVorgaenge = witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(cbVorgang1.getCbId(),
                cbVorgang1.getAuftragId());
        assertNotEmpty(cbVorgaenge, "CBVorgaenge sollten vorhanden sein");
        assertEquals(cbVorgaenge.size(), 2);
    }

    public void testGetWitaCBVorgaengeForTVOrStornoAuftragIdOnly() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        WitaCBVorgang cbVorgang1 = getBuilder(WitaCBVorgangBuilder.class).withAuftragBuilder(auftragBuilder).build();
        WitaCBVorgang cbVorgang2 = getBuilder(WitaCBVorgangBuilder.class).withAuftragBuilder(auftragBuilder).build();
        cbVorgang2.setCbId(null);
        cbVorgang2.setTyp(CBVorgang.TYP_REX_MK);

        List<WitaCBVorgang> cbVorgaenge = witaTalOrderService.getWitaCBVorgaengeForTVOrStorno(null,
                cbVorgang1.getAuftragId());
        assertNotEmpty(cbVorgaenge, "CBVorgaenge sollten vorhanden sein");
        assertEquals(cbVorgaenge.size(), 1);
    }

    public void testGetRufnummernForWitaCBVorgang() throws Exception {
        AccStandortDataBuilder standortData = standortDataBuilderProvider.get();
        CreatedData createdData = acceptanceTestDataBuilderProvider.get().withHurricanProduktId(Produkt.AK_CONNECT)
                .withStandortData(standortData)
                .build(getSessionId());
        flushAndClear();

        Set<Long> rufnummerIds = new HashSet<Long>();
        rufnummerIds.add(new Long(12345L));
        rufnummerIds.add(new Long(1234567L));
        rufnummerIds.add(new Long(123456789L));

        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragIds(
                        ImmutableList.of(createdData.auftrag.getAuftragId()))
                .withCbId(createdData.carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(Date.from(createdData.vorgabeMnet.atZone(ZoneId.systemDefault()).toInstant()))
                .withCbVorgangTyp(CBVorgang.TYP_NEU)
                .withUser(createdData.user)
                .withRufnummerIds(rufnummerIds)
                .withAutomation(Boolean.FALSE);

        List<CBVorgang> result = witaTalOrderService.createCBVorgang(cbvData);
        flushAndClear();
        assertNotEmpty(result);
        WitaCBVorgang createdCBVorgang = (WitaCBVorgang) result.get(0);
        Set<Long> storredDNs = createdCBVorgang.getRufnummerIds();
        assertThat(createdCBVorgang.getRufnummerIds(), hasSize(3));
        assertThat(new Long(12345L), isIn(storredDNs));
        assertThat(new Long(123456789L), isIn(storredDNs));
    }


    @DataProvider(name = "findCBVorgaenge4KlammerDataProvider")
    Object[][] findCBVorgaenge4KlammerDataProvider() {
        // @formatter:off
        return new Object[][]{
                { false,    false,  0 },
                { false,    true,   0 },
                { true,     false,  3 },
                { true,     true,   2 }
            };
        // @formatter:on
    }

    @Test(dataProvider = "findCBVorgaenge4KlammerDataProvider")
    public void testFindCBVorgaenge4Klammer(boolean withWitaKlammer, boolean excludeOrder, int expectedSize) {
        Long witaKlammer = withWitaKlammer ? cbVorgangDao.getNextAuftragsKlammer() : null;

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        getBuilder(WitaCBVorgangBuilder.class).withAuftragBuilder(auftragBuilder).withAuftragsKlammer(witaKlammer).build();
        getBuilder(WitaCBVorgangBuilder.class).withAuftragsKlammer(witaKlammer).build();
        getBuilder(WitaCBVorgangBuilder.class).withAuftragsKlammer(witaKlammer).build();

        Long auftragIdToExclude = excludeOrder ? Long.valueOf(auftragBuilder.get().getAuftragId()) : null;

        List<WitaCBVorgang> witaCbVorgaenge = witaTalOrderService.findCBVorgaenge4Klammer(witaKlammer, auftragIdToExclude);
        assertNotNull(witaCbVorgaenge);
        assertEquals(witaCbVorgaenge.size(), expectedSize);
    }

    @Test(expectedExceptions = { FindException.class })
    public void testFindWitaCBVorgaengeForAutomationExpectException() throws FindException {
        witaTalOrderService.findWitaCBVorgaengeForAutomation();
    }

    @DataProvider(name = "findCBVorgaengeForAutomationDataProvider")
    Object[][] findCBVorgaengeForAutomationDataProvider() {
        Long[] orderType1 = { CBVorgang.TYP_KUENDIGUNG };
        Long[] orderType2 = { CBVorgang.TYP_KUENDIGUNG, CBVorgang.TYP_REX_MK };
        // @formatter:off
        return new Object[][]{
                { orderType1, 1 },
                { orderType2, 2 }
            };
        // @formatter:on
    }

    @Test(dataProvider = "findCBVorgaengeForAutomationDataProvider")
    public void testFindWitaCBVorgaengeForAutomation(Long[] orderTypes, int found) throws FindException {
        // Should be found
        List<WitaCBVorgang> cbVorgaenge4Automation = new ArrayList<>();
        for (Long orderType : orderTypes) {
            cbVorgaenge4Automation.add(
                    buildWitaCBVorgang4Automation(CBVorgang.STATUS_ANSWERED, Boolean.TRUE, AenderungsKennzeichen.STANDARD, Boolean.TRUE, orderType));
        }
        // Should not be found
        buildWitaCBVorgang4Automation(CBVorgang.STATUS_SUBMITTED, null, AenderungsKennzeichen.STANDARD, Boolean.TRUE, CBVorgang.TYP_KUENDIGUNG);
        buildWitaCBVorgang4Automation(CBVorgang.STATUS_ANSWERED, Boolean.FALSE, AenderungsKennzeichen.STANDARD, Boolean.TRUE, CBVorgang.TYP_KUENDIGUNG);
        buildWitaCBVorgang4Automation(CBVorgang.STATUS_ANSWERED, Boolean.TRUE, AenderungsKennzeichen.TERMINVERSCHIEBUNG, Boolean.TRUE, CBVorgang.TYP_KUENDIGUNG);
        buildWitaCBVorgang4Automation(CBVorgang.STATUS_ANSWERED, Boolean.TRUE, AenderungsKennzeichen.STANDARD, Boolean.FALSE, CBVorgang.TYP_KUENDIGUNG);
        buildWitaCBVorgang4Automation(CBVorgang.STATUS_ANSWERED, Boolean.TRUE, AenderungsKennzeichen.STANDARD, Boolean.TRUE, CBVorgang.TYP_STORNO);

        List<WitaCBVorgang> result = witaTalOrderService.findWitaCBVorgaengeForAutomation(orderTypes);

        assertNotNull(result);
        assertNotEmpty(result);
        assertTrue(result.size() >= found);

        for (WitaCBVorgang toFind : cbVorgaenge4Automation) {
            boolean foundExpectedCbVorgang = false;
            for (WitaCBVorgang witaCbVorgang : result) {
                if (NumberTools.equal(toFind.getId(), witaCbVorgang.getId())) {
                    foundExpectedCbVorgang = true;
                    assertEquals(witaCbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
                    assertEquals(witaCbVorgang.getReturnOk(), Boolean.TRUE);
                    assertEquals(witaCbVorgang.getAenderungsKennzeichen(), AenderungsKennzeichen.STANDARD);
                    assertEquals(witaCbVorgang.getAutomation(), Boolean.TRUE);
                }
            }

            assertTrue(foundExpectedCbVorgang, String.format("WitaCbVorgang %s not found in automation list!", toFind.getId()));
        }
    }

    private WitaCBVorgang buildWitaCBVorgang4Automation(Long status, Boolean returnOK, AenderungsKennzeichen aKennzeichen, Boolean automation, Long typ) {
        WitaCBVorgangBuilder witaCbVorgangBuilder = getBuilder(WitaCBVorgangBuilder.class)
                .withStatus(status)
                .withReturnOk(returnOK)
                .withAenderungsKennzeichen(aKennzeichen)
                .withAutomation(automation)
                .withTyp(typ);
        if (CBVorgang.STATUS_ANSWERED.equals(status)) {
            witaCbVorgangBuilder.withAnsweredAt(new Date());
        }
        return witaCbVorgangBuilder.build();
    }
}
