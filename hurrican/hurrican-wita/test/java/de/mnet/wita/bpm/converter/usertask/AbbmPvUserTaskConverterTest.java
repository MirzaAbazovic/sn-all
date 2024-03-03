/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2011 16:05:44
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
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class AbbmPvUserTaskConverterTest extends BaseTest {

    @InjectMocks
    private final AbbmPvUserTaskConverter abbmPvUserTaskConverter = new AbbmPvUserTaskConverter();
    @Mock
    private WitaUsertaskService witaUsertaskService;

    @BeforeMethod
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public void testWrite() {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().build();
        AkmPvUserTask userTask = createUserTask(abbmPv);

        abbmPvUserTaskConverter.write(abbmPv);

        assertEquals(userTask.getAkmPvStatus(), AkmPvStatus.ABBM_PV_EMPFANGEN);
        assertEquals(userTask.getStatus(), UserTaskStatus.OFFEN);
        assertNull(userTask.getUserId());
        assertNull(userTask.getAntwortFrist());
        verify(witaUsertaskService).storeUserTask(userTask);
    }

    public void testWriteWithoutExistingUserTask() {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().build();

        AkmPvUserTask akmPvUserTask = new AkmPvUserTask();
        when(witaUsertaskService.createAkmPvUserTask(abbmPv)).thenReturn(akmPvUserTask);

        abbmPvUserTaskConverter.write(abbmPv);

        verify(witaUsertaskService).findAkmPvUserTask(abbmPv.getExterneAuftragsnummer());
        verify(witaUsertaskService).createAkmPvUserTask(abbmPv);
        verify(witaUsertaskService).storeUserTask(akmPvUserTask);
        verifyNoMoreInteractions(witaUsertaskService);
    }

    public void shouldNotWriteKuendigungsDatum() {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().build();
        AkmPvUserTask userTask = createUserTask(abbmPv);
        Date empfangsDatum = new Date();
        userTask.setEmpfangsDatum(empfangsDatum);
        LocalDate geplantesKuendigungsDatum = LocalDate.now();
        userTask.setGeplantesKuendigungsDatum(geplantesKuendigungsDatum);

        abbmPvUserTaskConverter.write(abbmPv);

        assertNull(userTask.getKuendigungsDatum());
        assertSame(userTask.getEmpfangsDatum(), empfangsDatum);
        assertSame(userTask.getGeplantesKuendigungsDatum(), geplantesKuendigungsDatum);

    }

    public void resetWiedervorlageDatum() {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().build();
        AkmPvUserTask userTask = createUserTask(abbmPv);
        userTask.setWiedervorlageAm(new Date());

        abbmPvUserTaskConverter.write(abbmPv);

        assertNull(userTask.getWiedervorlageAm());
    }

    private AkmPvUserTask createUserTask(AbbruchMeldungPv abbmPv) {
        AkmPvUserTask userTask = new AkmPvUserTask();
        userTask.changeAkmPvStatus(RUEM_PV_GESENDET);
        AKUser user = new AKUserBuilder().withRandomId().withName("hans").withLoginName("hans").get();
        userTask.setBearbeiter(user);
        when(witaUsertaskService.findAkmPvUserTask(abbmPv.getExterneAuftragsnummer())).thenReturn(userTask);
        return userTask;
    }

}
