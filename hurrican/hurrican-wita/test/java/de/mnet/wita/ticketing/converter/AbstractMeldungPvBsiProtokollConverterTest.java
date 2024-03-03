/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.02.2012 17:35:32
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public abstract class AbstractMeldungPvBsiProtokollConverterTest<T extends Meldung<?>, S extends AbstractMwfBsiProtokollConverter<T>>
        extends BaseMeldungBsiProtokollConverterTest<T, S> {

    private static final Logger LOGGER = Logger.getLogger(AbstractMeldungPvBsiProtokollConverterTest.class);

    @Mock
    private WitaUsertaskService witaUsertaskService;

    public void bemerkungShouldBeSetForAbbmPv() {
        T meldung = createMeldung();
        setupUserTask(meldung);
        AddCommunication protokollEintrag = protokollConverter.apply(meldung);

        LOGGER.debug(protokollEintrag.getNotes());

        assertNotEmpty(protokollEintrag.getNotes());
    }

    @Test(expectedExceptions = { RuntimeException.class })
    public void duplicateAuftragDatenRuntimeExceptionShouldBeThrown() {
        T meldung = createMeldung();
        AkmPvUserTask usertask = new AkmPvUserTask();
        UserTask2AuftragDaten usertask2Auftragdaten = new UserTask2AuftragDaten();
        usertask2Auftragdaten.setAuftragId(12354L);
        usertask.getUserTaskAuftragDaten().add(usertask2Auftragdaten);
        UserTask2AuftragDaten usertask2Auftragdaten2 = new UserTask2AuftragDaten();
        usertask2Auftragdaten2.setAuftragId(123546L);
        usertask.getUserTaskAuftragDaten().add(usertask2Auftragdaten2);
        when(witaUsertaskService.findAkmPvUserTask(meldung.getExterneAuftragsnummer())).thenReturn(usertask);

        protokollConverter.findHurricanAuftragId(meldung);
    }

    @Test(expectedExceptions = { RuntimeException.class })
    public void noUserTaskWasFoundRuntimeExceptionShouldBeThrown() {
        T meldung = createMeldung();
        AkmPvUserTask usertask = new AkmPvUserTask();
        when(witaUsertaskService.findAkmPvUserTask(meldung.getExterneAuftragsnummer())).thenReturn(usertask);

        protokollConverter.findHurricanAuftragId(meldung);
    }

    @Override
    public void externeAuftragsnummerShouldBeSet() {
        T meldung = createMeldung();
        setupUserTask(meldung);

        AddCommunication protokollEintrag = protokollConverter.apply(meldung);

        assertThat(protokollEintrag.getNotes(), containsString(meldung.getExterneAuftragsnummer()));
    }

    protected AkmPvUserTask setupUserTask(T meldung) {
        AkmPvUserTask usertask = new AkmPvUserTask();
        UserTask2AuftragDaten usertask2Auftragdaten = new UserTask2AuftragDaten();
        usertask2Auftragdaten.setAuftragId(12354L);
        usertask.getUserTaskAuftragDaten().add(usertask2Auftragdaten);
        when(witaUsertaskService.findAkmPvUserTask(meldung.getExterneAuftragsnummer())).thenReturn(usertask);
        return usertask;
    }
}
