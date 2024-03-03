/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.aggregator.MontageleistungAggregator;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.converter.MwfCbVorgangConverterService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public abstract class AbstractProcessingWitaTaskTest<T extends AbstractProcessingWitaTask> extends BaseTest {

    private final Class<T> clazz;

    @InjectMocks
    @Spy
    T underTest;

    @Mock
    protected MwfCbVorgangConverterService mwfCbVorgangConverterService;
    @Mock
    protected WorkflowTaskService workflowTaskService;
    @Mock
    protected CarrierElTALService carrierElTalService;
    @Mock
    protected MwfEntityDao mwfEntityDao;
    @Mock
    protected MwfEntityService mwfEntityService;
    @Mock
    protected MontageleistungAggregator montageleistungAggregator;
    @Mock
    protected WitaDataService witaDataService;
    @Mock
    protected CarrierService carrierService;
    @Mock
    protected WitaUsertaskService witaUsertaskService;
    @Mock
    protected WitaTalOrderService witaTalOrderService;
    @Mock
    protected WitaCheckConditionService witaCheckConditionService;
    @Mock
    protected AKUserService userService;

    protected WitaCBVorgang witaCBVorgang;

    protected AbstractProcessingWitaTaskTest(Class<T> clazz) {
        this.clazz = clazz;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        underTest = clazz.newInstance();
        MockitoAnnotations.initMocks(this);

        witaCBVorgang = (new WitaCBVorgangBuilder()).withCarrierRefNr("12300123").build();
    }

    protected DelegateExecution createExecution(WitaCBVorgang cbVorgang, MeldungsType messageType, Long witaInMwfId,
            Long witaOutMwfId, boolean workflowError) throws FindException {
        ExecutionImpl execution = new ExecutionImpl();
        execution.setProcessInstance(mock(ExecutionImpl.class));
        if (cbVorgang != null) {
            execution.setVariable(WitaTaskVariables.CB_VORGANG_ID.id, Long.valueOf(2));
            when(carrierElTalService.findCBVorgang(any(Long.class))).thenReturn(cbVorgang);
        }
        if (messageType != null) {
            execution.setVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id, messageType.name());
        }
        if (witaInMwfId != null) {
            execution.setVariable(WitaTaskVariables.WITA_IN_MWF_ID.id, witaInMwfId);
        }
        if (witaOutMwfId != null) {
            execution.setVariable(WitaTaskVariables.WITA_OUT_MWF_ID.id, witaOutMwfId);
        }
        execution.setVariable(WitaTaskVariables.WORKFLOW_ERROR.id, workflowError);
        return execution;
    }
}
