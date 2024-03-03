/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2011 15:43:42
 */
package de.mnet.wita.bpm.converter.usertask;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class AbmPvUserTaskConverterTest extends BaseTest {

    @InjectMocks
    private final AbmPvUserTaskConverter abmPvUserTaskConverter = new AbmPvUserTaskConverter();
    @InjectMocks
    private final AbbmPvUserTaskConverter abbmPvUserTaskConverter = new AbbmPvUserTaskConverter();

    @Mock
    private WitaUsertaskService witaUsertaskService;

    @BeforeMethod
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public void testWrite() {
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().build();
        AkmPvUserTask userTask = createUserTask(abmPv);

        abmPvUserTaskConverter.write(abmPv);

        verify(witaUsertaskService).storeUserTask(userTask);
        assertEquals(userTask.getAkmPvStatus(), ABM_PV_EMPFANGEN);
        assertEquals(userTask.getStatus(), UserTaskStatus.OFFEN);
        assertNull(userTask.getUserId());
        assertNull(userTask.getAntwortFrist());
    }

    public void testWriteWithoutExistingUserTask() {
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = new AkmPvUserTask();
        when(witaUsertaskService.createAkmPvUserTask(abmPv)).thenReturn(akmPvUserTask);

        abmPvUserTaskConverter.write(abmPv);

        verify(witaUsertaskService).findAkmPvUserTask(abmPv.getExterneAuftragsnummer());
        verify(witaUsertaskService).createAkmPvUserTask(abmPv);
        verify(witaUsertaskService).storeUserTask(akmPvUserTask);
        verifyNoMoreInteractions(witaUsertaskService);
    }

    public void testWriteDatum() {
        LocalDate kuendigungsDatum = LocalDate.now();
        LocalDate geplantesKuendigungsDatum = kuendigungsDatum.minusDays(20);
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().withAufnehmenderProvider(
                new AufnehmenderProviderBuilder().withUebernahmeDatumVerbindlich(kuendigungsDatum).build()).build();

        AkmPvUserTask userTask = createUserTask(abmPv);
        userTask.setGeplantesKuendigungsDatum(geplantesKuendigungsDatum);

        abmPvUserTaskConverter.write(abmPv);

        assertEquals(userTask.getGeplantesKuendigungsDatum(), geplantesKuendigungsDatum);
        assertEquals(userTask.getKuendigungsDatum(), kuendigungsDatum);
    }

    public void writeAbbmOnAbm() {
        AbbruchMeldungPv abbm = new AbbruchMeldungPvBuilder().build();
        AuftragsBestaetigungsMeldungPv abm = new AuftragsBestaetigungsMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = createUserTask(abm);

        abmPvUserTaskConverter.write(abm);
        abbmPvUserTaskConverter.write(abbm);

        assertTrue(akmPvUserTask.getAbbmOnAbm());
    }

    public void wiedervorlageReset() {
        AbbruchMeldungPv abbm = new AbbruchMeldungPvBuilder().build();
        AuftragsBestaetigungsMeldungPv abm = new AuftragsBestaetigungsMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = createUserTask(abm);
        akmPvUserTask.setWiedervorlageAm(new Date());

        abmPvUserTaskConverter.write(abm);
        abbmPvUserTaskConverter.write(abbm);

        assertNull(akmPvUserTask.getWiedervorlageAm());
    }

    public void writeAbm() {
        AuftragsBestaetigungsMeldungPv abm = new AuftragsBestaetigungsMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = createUserTask(abm);

        abmPvUserTaskConverter.write(abm);

        assertFalse(akmPvUserTask.getAbbmOnAbm());
    }


    @DataProvider(name = "writeSecondAbmPvDataProvider")
    public Object[][] writeSecondAbmPvDataProvider() {
        return new Object[][]{
                { AkmPvUserTask.AkmPvStatus.ABM_PV_EMPFANGEN, true },
                { AkmPvUserTask.AkmPvStatus.ABBM_PV_EMPFANGEN, false },
                { AkmPvUserTask.AkmPvStatus.RUEM_PV_GESENDET, false },
                { AkmPvUserTask.AkmPvStatus.AKM_PV_EMPFANGEN, false },
        };
    }


    @Test(dataProvider = "writeSecondAbmPvDataProvider")
    public void writeSecondAbmPv(AkmPvUserTask.AkmPvStatus currentAkmPvStatus, boolean expectTvFlagSet) {
        AuftragsBestaetigungsMeldungPv abm = new AuftragsBestaetigungsMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = createUserTask(abm);
        akmPvUserTask.changeAkmPvStatus(currentAkmPvStatus);
        akmPvUserTask.setBearbeiter(new AKUserBuilder().build());
        akmPvUserTask.setStatus(UserTaskStatus.GESCHLOSSEN);
        
        when(witaUsertaskService.findAkmPvUserTask(anyString())).thenReturn(akmPvUserTask);

        abmPvUserTaskConverter.write(abm);

        if (expectTvFlagSet) {
            assertTrue(akmPvUserTask.getTerminverschiebung());
            assertEquals(akmPvUserTask.getAkmPvStatus(), AkmPvUserTask.AkmPvStatus.ABM_PV_EMPFANGEN);
            assertNull(akmPvUserTask.getBearbeiter());
            assertEquals(akmPvUserTask.getStatus(), UserTaskStatus.OFFEN);
        }
        else {
            assertNull(akmPvUserTask.getTerminverschiebung());
        }
    }


    public void writeAbbm() {
        AbbruchMeldungPv abbm = new AbbruchMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = createUserTask(abbm);

        abbmPvUserTaskConverter.write(abbm);

        assertFalse(akmPvUserTask.getAbbmOnAbm());
    }

    private AkmPvUserTask createUserTask(Meldung<?> abm) {
        AkmPvUserTask akmPvUserTask = new AkmPvUserTask();
        akmPvUserTask.changeAkmPvStatus(RUEM_PV_GESENDET);
        AKUser hans = new AKUserBuilder().withRandomId().withName("hans").withLoginName("hans").setPersist(false).get();
        akmPvUserTask.setBearbeiter(hans);
        when(witaUsertaskService.findAkmPvUserTask(abm.getExterneAuftragsnummer())).thenReturn(akmPvUserTask);
        return akmPvUserTask;
    }
}
