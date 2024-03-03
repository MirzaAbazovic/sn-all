/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2010 11:07:47
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * TestNG Klasse fuer {@link SplitVerlaufCommand}
 */
@Test(groups = BaseTest.UNIT)
public class SplitVerlaufCommandTest extends BaseTest {

    private static final int SPLIT_DEFAULT = 0;
    private static final int SPLIT_WITH_MAIN_NUMBER = 1;
    private static final int SPLIT_ALL_WITH_MAIN_NUMBER = 2;

    private SplitVerlaufCommand cut;
    private BAService baServiceMock;
    private CCLeistungsService leistungsServiceMock;
    private AuftragBuilder auftragBuilder;
    private Verlauf verlauf;
    private List<VerlaufAbteilung> verlaufAbteilungen;
    private Set<Long> orderIdsToRemove;

    private void buildTestData(int variante) throws FindException {
        cut = new SplitVerlaufCommand();

        auftragBuilder = new AuftragBuilder().withRandomId().setPersist(false);

        Set<Long> subOrderIds = new HashSet<Long>();
        subOrderIds.add(Long.valueOf(100));
        subOrderIds.add(Long.valueOf(101));
        subOrderIds.add(Long.valueOf(102));

        orderIdsToRemove = new HashSet<Long>();
        switch (variante) {
            case SPLIT_DEFAULT:
                orderIdsToRemove.add(Long.valueOf(100));
                orderIdsToRemove.add(Long.valueOf(101));
                break;
            case SPLIT_WITH_MAIN_NUMBER:
                orderIdsToRemove.add(auftragBuilder.get().getAuftragId());
                break;
            case SPLIT_ALL_WITH_MAIN_NUMBER:
                orderIdsToRemove.add(auftragBuilder.get().getAuftragId());
                orderIdsToRemove.add(Long.valueOf(100));
                orderIdsToRemove.add(Long.valueOf(101));
                break;
        }

        verlauf = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withVerlaufStatusId(VerlaufStatus.BEI_DISPO)
                .withSubAuftragsIds(subOrderIds)
                .setPersist(false).build();
        cut.setVerlaufToSplit(verlauf);
        cut.setOrderIdsToRemove(orderIdsToRemove);

        baServiceMock = mock(BAService.class);
        cut.setBaService(baServiceMock);

        leistungsServiceMock = mock(CCLeistungsService.class);
        cut.setCcLeistungsService(leistungsServiceMock);

        verlaufAbteilungen = new ArrayList<VerlaufAbteilung>();

        VerlaufAbteilung verlaufAbtStVoice = new VerlaufAbteilungBuilder()
                .withAbteilungId(Abteilung.ST_VOICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(verlaufAbtStVoice);

        VerlaufAbteilung verlaufAbtFieldService = new VerlaufAbteilungBuilder()
                .withAbteilungId(Abteilung.FIELD_SERVICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(verlaufAbtFieldService);

        when(baServiceMock.findVerlaufAbteilungen(verlauf.getId())).thenReturn(verlaufAbteilungen);
        when(leistungsServiceMock.findAuftrag2TechLeistungen4Verlauf(any(Long.class))).thenReturn(null);
    }


    public void testSplitSubOrders() throws FindException, StoreException {
        buildTestData(SPLIT_DEFAULT);
        cut.splitVerlauf();

        assertEquals(verlauf.getAuftragId(), auftragBuilder.get().getAuftragId(), "Haupt-Auftrag fuer Verlauf nicht mehr identisch!");
        assertFalse(verlauf.getSubAuftragsIds().containsAll(orderIdsToRemove), "Verlauf wurde nicht getrennt!");
    }


    public void testSplitSubOrdersUseMainOrder() throws FindException, StoreException {
        buildTestData(SPLIT_WITH_MAIN_NUMBER);
        cut.splitVerlauf();

        assertFalse(NumberTools.equal(verlauf.getAuftragId(), auftragBuilder.get().getAuftragId()),
                "Haupt-Auftrag fuer Verlauf ist immer noch identisch - Trennung nicht erfolgt!");
    }

    public void testSplitSubOrdersUseMainOrderAndExpectCompleteSplit() throws FindException, StoreException {
        buildTestData(SPLIT_ALL_WITH_MAIN_NUMBER);
        cut.splitVerlauf();

        assertEquals(verlauf.getAuftragId(), auftragBuilder.get().getAuftragId(), "Haupt-Auftrag fuer Verlauf nicht mehr identisch!");
        assertTrue(CollectionTools.isEmpty(verlauf.getSubAuftragsIds()), "Verlauf wurde nicht vollstaendig getrennt!");
    }


    public void testSwitchAuftrag2TechLs() throws FindException, StoreException {
        Long verlaufIdToSplit = Long.valueOf(1);
        Long verlaufIdNew = Long.valueOf(2);
        AuftragBuilder auftragBuilder1 = new AuftragBuilder().withRandomId().setPersist(false);
        AuftragBuilder auftragBuilder2 = new AuftragBuilder().withRandomId().setPersist(false);

        Auftrag2TechLeistung a2tlToSwitch = new Auftrag2TechLeistungBuilder()
                .withAuftragBuilder(auftragBuilder1)
                .withVerlaufIdReal(verlaufIdToSplit)
                .setPersist(false).build();
        Auftrag2TechLeistung a2tlToSwitchKuend = new Auftrag2TechLeistungBuilder()
                .withAuftragBuilder(auftragBuilder1)
                .withVerlaufIdKuend(verlaufIdToSplit)
                .setPersist(false).build();
        Auftrag2TechLeistung a2tlToStay = new Auftrag2TechLeistungBuilder()
                .withAuftragBuilder(auftragBuilder2)
                .withVerlaufIdReal(verlaufIdToSplit)
                .setPersist(false).build();
        List<Auftrag2TechLeistung> auftrag2TechLeistungen = new ArrayList<Auftrag2TechLeistung>();
        auftrag2TechLeistungen.add(a2tlToSwitch);
        auftrag2TechLeistungen.add(a2tlToSwitchKuend);
        auftrag2TechLeistungen.add(a2tlToStay);

        when(leistungsServiceMock.findAuftrag2TechLeistungen4Verlauf(verlaufIdToSplit)).thenReturn(auftrag2TechLeistungen);

        cut.switchAuftrag2TechLs(auftragBuilder1.get().getAuftragId(), verlaufIdToSplit, verlaufIdNew);

        assertEquals(a2tlToStay.getVerlaufIdReal(), verlaufIdToSplit);
        assertEquals(a2tlToSwitch.getVerlaufIdReal(), verlaufIdNew);
        assertEquals(a2tlToSwitchKuend.getVerlaufIdKuend(), verlaufIdNew);
    }

}


