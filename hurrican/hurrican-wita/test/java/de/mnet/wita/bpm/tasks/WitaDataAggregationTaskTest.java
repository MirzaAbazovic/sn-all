/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2011 11:28:58
 */
package de.mnet.wita.bpm.tasks;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.aggregator.AuftragsKennerAggregator;
import de.mnet.wita.aggregator.GeschaeftsfallAggregator;
import de.mnet.wita.aggregator.KundeAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerKueKdAggregator;
import de.mnet.wita.aggregator.ProjektAggregator;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class WitaDataAggregationTaskTest extends BaseTest {

    @InjectMocks
    private WitaDataAggregationTask underTest;

    @Mock
    private WorkflowTaskService workflowTaskService;
    @Mock
    private CarrierElTALService carrierElTALService;
    @Mock
    private WitaConfigService witaConfigService;
    @Mock
    private KundeAggregator kundeAggregator;
    @Mock
    private GeschaeftsfallAggregator geschaeftsfallAggregator;
    @SuppressWarnings("unused") // used by InjectMocks
    @Mock
    private AuftragsKennerAggregator auftragsKennerAggregator;
    @SuppressWarnings("unused") // used by InjectMocks
    @Mock
    private ProjektAggregator projektAggregator;
    @Mock
    private ProduktBezeichnerAggregator produktBezeichnerAggregator;
    @Mock
    private ProduktBezeichnerKueKdAggregator produktBezeichnerKueKdAggregator;
    @SuppressWarnings("unused") // used by InjectMocks
    @Mock
    private WorkflowTaskValidationService workflowTaskValidationService;

    private final WitaCBVorgang cbVorgang = new WitaCBVorgang();

    @BeforeMethod
    public void setUp() {
        underTest = new WitaDataAggregationTask();
        MockitoAnnotations.initMocks(this);

    }

    public void testRestart() throws Exception {
        DelegateExecution execution = new ExecutionImpl();
        execution.setVariable(WitaTaskVariables.RESTART.id, true);

        underTest.execute(execution);

        verifyZeroInteractions(workflowTaskService);
    }

    public void testAggregateProduktBezeichner() throws Exception {
        prepareAggregate();

        when(produktBezeichnerAggregator.aggregate(any(Auftrag.class))).thenReturn(ProduktBezeichner.HVT_2H);

        assertAggregatedProduktBezeichnerEquals(ProduktBezeichner.HVT_2H);
    }

    public void testAggregateProduktBezeichnerKueKd() throws Exception {
        cbVorgang.setTyp(CBVorgang.TYP_KUENDIGUNG);

        prepareAggregate();

        when(produktBezeichnerKueKdAggregator.aggregate(any(Auftrag.class))).thenReturn(ProduktBezeichner.LWL);

        assertAggregatedProduktBezeichnerEquals(ProduktBezeichner.LWL);
    }

    private void prepareAggregate() {
        Pair<Kunde, Kunde> kundeAggregatorResult = new Pair<>(mock(Kunde.class), mock(Kunde.class));
        when(kundeAggregator.aggregate(cbVorgang)).thenReturn(kundeAggregatorResult);

        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(geschaeftsfallAggregator.aggregate(cbVorgang)).thenReturn(
                geschaeftsfall);

        // Produktbezeichner soll vor dem Aggregieren nicht gesetzt sein
        geschaeftsfall.getAuftragsPosition().setProduktBezeichner(null);
    }

    private void assertAggregatedProduktBezeichnerEquals(ProduktBezeichner produktBezeichner) throws Exception {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V1);
        underTest.execute(createExecution(cbVorgang));

        ArgumentCaptor<Auftrag> argumentCaptor = ArgumentCaptor.forClass(Auftrag.class);
        verify(workflowTaskService).sendToWita(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getGeschaeftsfall().getAuftragsPosition().getProduktBezeichner(),
                equalTo(produktBezeichner));
    }

    private DelegateExecution createExecution(WitaCBVorgang cbVorgang)
            throws FindException {
        DelegateExecution execution = new ExecutionImpl();
        execution.setVariable(WitaTaskVariables.CB_VORGANG_ID.id, 2L);
        execution.setVariable(WitaTaskVariables.WORKFLOW_ERROR.id, false);
        when(carrierElTALService.findCBVorgang(any(Long.class))).thenReturn(cbVorgang);
        return execution;
    }

}
