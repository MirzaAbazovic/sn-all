/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.tasks;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.converter.usertask.RuemPvUserTaskConverter;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class SendRuemPvTaskTest extends BaseTest {

    private static final Long AKM_ID = 123L;
    private static final String AKM_KUNDENNUMMER = "123";
    private static final String KUNDENNUMMER_BESTELLER = "5920312290";

    @InjectMocks
    private SendRuemPvTask underTest;

    @Mock
    private CarrierService carrierService;

    @Mock
    private WorkflowTaskService workflowTaskService;

    @Mock
    private MwfEntityDao mwfEntityDao;

    @Mock
    private WitaConfigService witaConfigService;

    @Mock
    private RuemPvUserTaskConverter ruemPvUserTaskConverter;

    @BeforeMethod
    public void setUp() throws FindException {
        underTest = new SendRuemPvTask();
        MockitoAnnotations.initMocks(this);

        CarrierKennung carrierKennung = new CarrierKennung();
        carrierKennung.setKundenNr(AKM_KUNDENNUMMER);
        carrierKennung.setBezeichnung("M-net");
        when(carrierService.findCarrierKennungen()).thenReturn(Collections.singletonList(carrierKennung));

    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void testWithoutMeldung() throws Exception {
        underTest.execute(new ExecutionImpl());
    }

    public void testLeitungAndAnschluss() throws Exception {
        final AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder().withKundennummer(AKM_KUNDENNUMMER)
                .build();
        when(mwfEntityDao.findById(AKM_ID, AnkuendigungsMeldungPv.class)).thenReturn(
                akmPv);

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RueckMeldungPv rueckMeldungPv = (RueckMeldungPv) invocation.getArguments()[0];
                assertThat(rueckMeldungPv.getKundenNummer(), equalTo(akmPv.getKundenNummer()));
                assertThat(rueckMeldungPv.getKundennummerBesteller(), equalTo(KUNDENNUMMER_BESTELLER));
                assertThat(rueckMeldungPv.getLeitung(), notNullValue());
                assertThat(rueckMeldungPv.getLeitung(), equalTo(akmPv.getLeitung()));
                assertThat(rueckMeldungPv.getAnschlussOnkz(), notNullValue());
                assertThat(rueckMeldungPv.getAnschlussRufnummer(), notNullValue());
                assertThat(rueckMeldungPv.getAnschlussOnkz(), equalTo(akmPv.getAnschlussOnkz()));
                assertThat(rueckMeldungPv.getAnschlussRufnummer(), equalTo(akmPv.getAnschlussRufnummer()));
                return null;
            }
        }).when(workflowTaskService).sendToWita(any(RueckMeldungPv.class));

        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.getDefault());
        underTest.execute(createExecution(RuemPvAntwortCode.OK));

        verify(workflowTaskService).sendToWita(any(RueckMeldungPv.class));
        verify(ruemPvUserTaskConverter).write(any(RueckMeldungPv.class));

    }

    private DelegateExecution createExecution(RuemPvAntwortCode ruemPvAntwortCode) {
        DelegateExecution execution = new ExecutionImpl();
        execution.setVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id, MeldungsType.RUEM_PV.name());
        execution.setVariable(WitaTaskVariables.RUEM_PV_ANTWORTCODE.id, ruemPvAntwortCode);
        execution.setVariable(WitaTaskVariables.AKM_PV_ID.id, AKM_ID);
        return execution;
    }

}
