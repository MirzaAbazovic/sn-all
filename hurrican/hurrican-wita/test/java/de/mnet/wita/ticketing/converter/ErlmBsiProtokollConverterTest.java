/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 12:59:43
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = UNIT)
public class ErlmBsiProtokollConverterTest extends
        BaseMeldungBsiProtokollConverterTest<ErledigtMeldung, ErlmBsiProtokollConverter> {

    private static final Logger LOGGER = Logger.getLogger(ErlmBsiProtokollConverterTest.class);

    @Mock
    private WitaUsertaskService witaUsertaskService;

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new ErlmBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void kuedtShouldBringOtherText() {
        ErledigtMeldung erlm = new ErledigtMeldungBuilder()
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_TELEKOM)
                .build();
        setupUserTask(erlm);

        AddCommunication protokollEintrag = protokollConverter.apply(erlm);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Leitung wurde von der DTAG gekündigt"));
    }

    private KueDtUserTask setupUserTask(ErledigtMeldung erlm) {
        KueDtUserTask kueDtUsertask = new KueDtUserTask();
        UserTask2AuftragDaten usertask2Auftragdaten = new UserTask2AuftragDaten();
        usertask2Auftragdaten.setAuftragId(12354L);
        kueDtUsertask.getUserTaskAuftragDaten().add(usertask2Auftragdaten);
        when(witaUsertaskService.findKueDtUserTask(erlm.getExterneAuftragsnummer())).thenReturn(kueDtUsertask);
        return kueDtUsertask;
    }

    @Override
    protected ErledigtMeldung createMeldung() {
        return new ErledigtMeldungBuilder().build();
    }

}
