package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.wholesale.ModifyPortPendingException;

@Test(groups = BaseTest.SERVICE)
public class AuftragAktionTest extends AbstractHurricanBaseServiceTest {
    private LocalDate now = LocalDate.now();
    private CCAuftragService auftragService;
    private AuftragBuilder auftragBuilder;
    private AuftragBuilder prevAuftragBuilder;

    @BeforeMethod
    protected void init() {
        auftragService = getCCService(CCAuftragService.class);
        auftragBuilder = getBuilder(AuftragBuilder.class);
        prevAuftragBuilder = getBuilder(AuftragBuilder.class);
    }

    public void save() throws StoreException, FindException {
        AuftragAktion aktion = getModifyAktionForTomorrow().setPersist(false).build();

        auftragService.saveAuftragAktion(aktion);
        assertNotNull(aktion.getId());
    }

    public void findNone() throws StoreException, FindException {
        getModifyAktion(now.minusDays(1)).build();

        AuftragAktion found = auftragService.getActiveAktion(auftragBuilder.get().getId(), AktionType.MODIFY_PORT);
        assertThat(found, nullValue());
    }

    public void findOne() throws StoreException, FindException {
        AuftragAktion aktion = getModifyAktionForTomorrow().build();

        AuftragAktion found = auftragService.getActiveAktion(auftragBuilder.get().getId(), AktionType.MODIFY_PORT);
        assertNotNull(found);
        assertEquals(found.getId(), aktion.getId());
    }

    @Test(expectedExceptions = ModifyPortPendingException.class)
    public void testModifyPendingException() throws StoreException {
        getModifyAktionForTomorrow().build();

        AuftragAktion aktion2 = getModifyAktion(now.plusDays(2)).setPersist(false).build();

        auftragService.saveAuftragAktion(aktion2);
        assertEquals(false, true);
    }

    @Test
    public void testModifyActiveAktion() throws StoreException {
        AuftragAktion aktion = getModifyAktionForTomorrow().build();
        LocalDate executionDateNew = aktion.getDesiredExecutionDate().plusDays(1);

        AuftragAktion modifiedAktion = auftragService.modifyActiveAktion(aktion.getAuftragId(), aktion.getAction(),
                executionDateNew);
        assertNotNull(modifiedAktion);
        assertTrue(modifiedAktion.getDesiredExecutionDate().equals(executionDateNew));
    }

    public void testModifyPendingAllowed() throws StoreException {
        getModifyAktion(now.minusDays(1)).build();

        AuftragAktion aktion2 = getModifyAktion(now.plusDays(2)).setPersist(false).build();

        auftragService.saveAuftragAktion(aktion2);
        assertNotNull(aktion2.getId());
    }

    public void testCancelAuftragAction() throws StoreException {
        AuftragAktion action = getModifyAktion(now).build();
        auftragService.cancelAuftragAktion(action);
        assertTrue(action.isCancelled());
    }

    private AuftragAktionBuilder getModifyAktionForTomorrow() {
        return getModifyAktion(now.plusDays(1));
    }

    private AuftragAktionBuilder getModifyAktion(LocalDate date) {
        return getBuilder(AuftragAktionBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withPreviousAuftragBuilder(prevAuftragBuilder)
                .withAction(AktionType.MODIFY_PORT)
                .withDesiredExecutionDate(date);
    }

}
