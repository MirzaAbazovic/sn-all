/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2010 09:04:08
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBuilder;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortViewBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;


/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeCpsExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeCpsExecuterTest extends BaseTest {

    private HWBaugruppenChangeCpsExecuter cut;
    private CPSService cpsService;
    private CCAuftragService auftragService;

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeCpsExecuter();
        cpsService = mock(CPSService.class);
        cut.setCpsService(cpsService);

        auftragService = mock(CCAuftragService.class);
        cut.setAuftragService(auftragService);
    }


    public void testIsCpsActionAllowed() {
        Reference statePrepared = new ReferenceBuilder().withId(HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED.refId()).setPersist(false).build();
        Reference stateExecuted = new ReferenceBuilder().withId(HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId()).setPersist(false).build();

        HWBaugruppenChange hwBgChange = new HWBaugruppenChangeBuilder()
                .withChangeTypeReference(new ReferenceBuilder().withId(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId()).setPersist(false).build())
                .withChangeStateReference(statePrepared)
                .setPersist(false)
                .build();

        cut.setHwBgChange(hwBgChange);
        cut.setCpsInit(true);

        // CPS Init bei noch nicht ausgefuehrter Planung --> true erwartet
        boolean allowed = cut.isCpsActionAllowed();
        assertTrue(allowed, "CPS-Init sollte erlaubt sein!");

        // CPS Init bei ausgefuehrter Planung --> false erwartet
        hwBgChange.setChangeState(stateExecuted);
        allowed = cut.isCpsActionAllowed();
        assertFalse(allowed, "CPS-Init sollte NICHT erlaubt sein!");

        // CPS Modify bei ausgefuehrter Planung --> true erwartet
        cut.setCpsInit(false);
        allowed = cut.isCpsActionAllowed();
        assertTrue(allowed, "CPS modify sollte erlaubt sein!");

        // CPS Modify bei noch nicht ausgefuehrter Planung --> false erwartet
        hwBgChange.setChangeState(statePrepared);
        allowed = cut.isCpsActionAllowed();
        assertFalse(allowed, "CPS modify sollte NICHT erlaubt sein!");
    }


    public void testFilterOrders4CpsInit() throws FindException {
        HWBaugruppenChangePort2PortView p2pViewWithCps = new HWBaugruppenChangePort2PortViewBuilder()
                .withLastSuccessfulCpsTx(Long.valueOf(1L))
                .withAuftragId(Long.valueOf(1000))
                .build();
        HWBaugruppenChangePort2PortView p2pViewWithoutCps = new HWBaugruppenChangePort2PortViewBuilder()
                .withAuftragId(Long.valueOf(1001))
                .build();

        List<HWBaugruppenChangePort2PortView> portMappingViews = Arrays.asList(p2pViewWithCps, p2pViewWithoutCps);

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(p2pViewWithoutCps.getAuftragId())
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();
        when(auftragService.findAuftragDatenByAuftragId(p2pViewWithoutCps.getAuftragId())).thenReturn(auftragDaten);

        Collection<Long> orderIds4CpsInit = cut.filterOrders4CpsInit(portMappingViews);

        assertNotNull(orderIds4CpsInit, "Es wurden keine Auftraege fuer CPS-Init ermittelt!");
        assertEquals(orderIds4CpsInit.size(), 1, "Anzahl Auftraege fuer CPS-Init ist ungueltig!");
        assertEquals(orderIds4CpsInit.iterator().next(), p2pViewWithoutCps.getAuftragId(), "Falscher Auftrag fuer CPS Init ermittelt!");
    }


    public void testFilterOrders4CpsModify() throws FindException {
        HWBaugruppenChangePort2PortView activeOrder = new HWBaugruppenChangePort2PortViewBuilder()
                .withAuftragId(Long.valueOf(1000))
                .build();
        HWBaugruppenChangePort2PortView inActiveOrder = new HWBaugruppenChangePort2PortViewBuilder()
                .withAuftragId(Long.valueOf(1001))
                .build();

        List<HWBaugruppenChangePort2PortView> portMappingViews = Arrays.asList(activeOrder, inActiveOrder);

        AuftragDaten auftragDatenActive = new AuftragDatenBuilder()
                .withAuftragId(activeOrder.getAuftragId())
                .withAuftragNoOrig(Long.valueOf(998))
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();

        AuftragDaten auftragDatenInActive = new AuftragDatenBuilder()
                .withAuftragId(inActiveOrder.getAuftragId())
                .withAuftragNoOrig(Long.valueOf(999))
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN)
                .setPersist(false).build();

        when(auftragService.findAuftragDatenByAuftragId(activeOrder.getAuftragId())).thenReturn(auftragDatenActive);
        when(auftragService.findAuftragDatenByAuftragId(inActiveOrder.getAuftragId())).thenReturn(auftragDatenInActive);

        Collection<Long> orderIds4CpsInit = cut.filterOrders4CpsModify(portMappingViews);

        assertNotNull(orderIds4CpsInit, "Es wurden keine Auftraege fuer CPS-Modify ermittelt!");
        assertEquals(orderIds4CpsInit.size(), 1, "Anzahl Auftraege fuer CPS-Modify ist ungueltig!");
        assertEquals(orderIds4CpsInit.iterator().next(), activeOrder.getAuftragId(), "Falscher Auftrag fuer CPS Modify ermittelt!");
    }


    public void testIsOrderValid4CpsTx() {
        AuftragDaten auftragDatenActive = new AuftragDatenBuilder()
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .setPersist(false).build();

        AuftragDaten auftragDatenCancelled = new AuftragDatenBuilder()
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .setPersist(false).build();

        AuftragDaten auftragDatenPreparing = new AuftragDatenBuilder()
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN)
                .setPersist(false).build();

        boolean valid = cut.isOrderValid4CpsTx(auftragDatenActive);
        assertTrue(valid);

        valid = cut.isOrderValid4CpsTx(auftragDatenCancelled);
        assertFalse(valid);

        valid = cut.isOrderValid4CpsTx(auftragDatenPreparing);
        assertFalse(valid);
    }

}


