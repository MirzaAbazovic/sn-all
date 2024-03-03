/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2010 08:09:17
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * TestNG Klasse fuer {@link StornoVerlaufCommand}
 */
@Test(groups = BaseTest.UNIT)
public class StornoVerlaufCommandTest extends BaseTest {

    private StornoVerlaufCommand cut;
    private AKUser user;
    private BAService baServiceMock;
    private Verlauf verlauf;
    private List<VerlaufAbteilung> verlaufAbteilungen;

    @BeforeMethod(groups = "unit")
    public void prepareTest() throws FindException {
        cut = new StornoVerlaufCommand();

        user = new AKUser();
        user.setDepartmentId(AKDepartment.DEP_AM);
        cut.setUser(user);

        verlauf = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .withVerlaufStatusId(VerlaufStatus.BEI_DISPO)
                .setPersist(false).build();
        cut.setVerlauf(verlauf);

        baServiceMock = mock(BAService.class);
        cut.baService = baServiceMock;

        verlaufAbteilungen = new ArrayList<VerlaufAbteilung>();

        VerlaufAbteilung verlaufAbtStVoice = new VerlaufAbteilungBuilder()
                .withAbteilungId(Abteilung.ST_VOICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(verlaufAbtStVoice);

        VerlaufAbteilung verlaufAbtFieldService = new VerlaufAbteilungBuilder()
                .withAbteilungId(Abteilung.FIELD_SERVICE)
                .setPersist(false).build();
        verlaufAbteilungen.add(verlaufAbtFieldService);
    }

    public void testCheckStatusAMOk() throws FindException {
        cut.checkStatus();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckStatusAMExceptionBecauseOfFieldService() throws FindException {
        verlauf.setVerlaufStatusId(VerlaufStatus.BEI_TECHNIK);
        when(baServiceMock.findVerlaufAbteilungen(verlauf.getId())).thenReturn(verlaufAbteilungen);

        cut.checkStatus();
    }

    public void testCheckStatusDispo() throws FindException {
        user.setDepartmentId(AKDepartment.DEP_DISPO);
        cut.checkStatus();
    }

    public void testChangeOrderStates() throws FindException, StoreException {
        Set<Long> orderIds = new HashSet<Long>();
        orderIds.add(Long.valueOf(100));
        orderIds.add(Long.valueOf(101));

        AuftragDaten ad1 = new AuftragDatenBuilder().withId(verlauf.getAuftragId()).setPersist(false).build();
        AuftragDaten ad2 = new AuftragDatenBuilder().withId(Long.valueOf(100)).setPersist(false).build();
        AuftragDaten ad3 = new AuftragDatenBuilder().withId(Long.valueOf(101)).setPersist(false).build();

        CCAuftragService auftragServiceMock = mock(CCAuftragService.class);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(verlauf.getAuftragId())).thenReturn(ad1);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(Long.valueOf(100))).thenReturn(ad2);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(Long.valueOf(101))).thenReturn(ad3);

        verlauf.setSubAuftragsIds(orderIds);
        verlauf.setStatusIdAlt(AuftragStatus.ERFASSUNG_SCV);
        cut.ccAuftragService = auftragServiceMock;
        cut.changeOrderStates();

        assertEquals(ad1.getStatusId(), AuftragStatus.ERFASSUNG_SCV, "Status von Haupt-Auftrag nicht i.O.");
        assertEquals(ad2.getStatusId(), AuftragStatus.ERFASSUNG_SCV, "Status von Sub-Auftrag 1 nicht i.O.");
        assertEquals(ad3.getStatusId(), AuftragStatus.ERFASSUNG_SCV, "Status von Sub-Auftrag 2 nicht i.O.");
    }

}


