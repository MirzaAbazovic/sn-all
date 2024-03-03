/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.tasks;

import static org.mockito.Mockito.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class SendMessageTaskTest extends BaseTest {

    @InjectMocks
    private SendMessageTask underTest;

    @Mock
    private WorkflowTaskService workflowTaskService;
    @Mock
    private MwfEntityDao mwfEntityDao;
    @Mock
    private WitaConfigService witaConfigService;

    @Mock
    private Storno stornoMock;

    @Mock
    private TerminVerschiebung terminVerschiebungMock;

    @BeforeMethod
    public void setUp() throws Exception {
        underTest = new SendMessageTask();

        MockitoAnnotations.initMocks(this);
    }

    public void testStorno() throws Exception {
        testSendRequest(123123124L, MeldungsType.STORNO, stornoMock, Storno.class, WitaCdmVersion.V1);
    }

    public void testTv() throws Exception {
        testSendRequest(123123125L, MeldungsType.TV, terminVerschiebungMock, TerminVerschiebung.class, WitaCdmVersion.V1);
    }

    private void testSendRequest(Long requestId, MeldungsType meldungsType, MnetWitaRequest request, Class requestClass, WitaCdmVersion witaCdmVersion) throws Exception {
        when(mwfEntityDao.findById(requestId, requestClass)).thenReturn(request);
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(witaCdmVersion);

        underTest.execute(createExecution(meldungsType, requestId, false));

        verify(mwfEntityDao).findById(requestId, requestClass);
        verify(workflowTaskService).sendToWita(eq(request));
        verify(request).setCdmVersion(eq(witaCdmVersion));
        verifyNoMoreInteractions(mwfEntityDao, workflowTaskService);
    }

    @DataProvider
    public Object[][] dataProviderMeldungShouldThrowException() {
        // @formatter:off
        return new Object[][] {
                { MeldungsType.QEB },
                { MeldungsType.VZM },
                { MeldungsType.ABM },
                { MeldungsType.ABBM },
                { MeldungsType.ERLM },
                { MeldungsType.ENTM },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderMeldungShouldThrowException", expectedExceptions = WitaBpmException.class)
    public void testMeldungShouldThrowException(MeldungsType meldungsType) throws Exception {
        underTest.execute(createExecution(meldungsType, 1L, false));
    }

    private DelegateExecution createExecution(MeldungsType messageType, Long witaOutMwfId, boolean workflowError) {
        ExecutionImpl execution = new ExecutionImpl();
        execution.setProcessInstance(mock(ExecutionImpl.class));
        execution.setVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id, messageType.name());
        execution.setVariable(WitaTaskVariables.WITA_OUT_MWF_ID.id, witaOutMwfId);
        execution.setVariable(WitaTaskVariables.WORKFLOW_ERROR.id, workflowError);
        return execution;
    }
}
