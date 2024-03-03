/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2011 12:50:45
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.bpm.converter.usertask.RuemPvUserTaskConverter;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AbgebenderProviderBuilder;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class RuemPvUserTaskConverterTest extends BaseTest {

    @InjectMocks
    private final
    RuemPvUserTaskConverter ruemPvUserTaskConverter = new RuemPvUserTaskConverter();

    @Mock
    private
    WitaUsertaskService witaUsertaskService;

    @BeforeMethod
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    public void testWrite() {
        RueckMeldungPv ruemPv = new RueckMeldungPvBuilder().build();

        AkmPvUserTask userTask = createUserTask(ruemPv);

        ruemPvUserTaskConverter.write(ruemPv);

        verify(witaUsertaskService).findAkmPvUserTask(ruemPv.getExterneAuftragsnummer());
        verify(witaUsertaskService).storeUserTask(userTask);

        assertEquals(userTask.getAkmPvStatus(), AkmPvStatus.RUEM_PV_GESENDET);
        assertEquals(userTask.getStatus(), UserTaskStatus.GESCHLOSSEN);
    }

    public void geplantesKuendiungsDatumShouldNotBeSet() {
        RueckMeldungPv ruemPv =
                new RueckMeldungPvBuilder()
                        .withAbgebenderProvider(new AbgebenderProviderBuilder().build())
                        .build();

        AkmPvUserTask userTask = createUserTask(ruemPv);

        ruemPvUserTaskConverter.write(ruemPv);

        assertNull(userTask.getGeplantesKuendigungsDatum());
    }

    private AkmPvUserTask createUserTask(RueckMeldungPv ruemPv) {
        AkmPvUserTask userTask = new AkmPvUserTask();
        when(witaUsertaskService.findAkmPvUserTask(ruemPv.getExterneAuftragsnummer())).thenReturn(userTask);
        return userTask;
    }

}
