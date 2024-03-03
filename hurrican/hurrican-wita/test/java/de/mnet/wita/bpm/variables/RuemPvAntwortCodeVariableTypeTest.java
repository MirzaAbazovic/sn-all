/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 11:23:42
 */
package de.mnet.wita.bpm.variables;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.ImmutableMap;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.impl.CommonWorkflowServiceImpl;
import de.mnet.wita.bpm.impl.Workflow;
import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;

@Test(groups = BaseTest.SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml", })
public class RuemPvAntwortCodeVariableTypeTest extends AbstractActivitiBaseTest {

    private static final String AKM_PV_EXTAUFTRAGSNUMMER = "123";
    private static final Long AKM_PV_ID = 567L;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * Mocked as spy by spring context
     */
    @Autowired
    private RuemPvAntwortCodeVariableType ruemPvAntwortCodeVariableTypeSpy;

    /**
     * Mocked as spy by spring context
     */
    @Autowired
    private
    MwfEntityDao mwfEntityDao;

    @BeforeMethod
    public void setUp() {
        reset(ruemPvAntwortCodeVariableTypeSpy);
        CommonWorkflowServiceImpl commonWorkflowServiceImpl = new CommonWorkflowServiceImpl();
        commonWorkflowServiceImpl.setRuntimeService(runtimeService);
        commonWorkflowService = commonWorkflowServiceImpl;
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void checkVariableTypeUsed() {
        WitaCdmVersion witaCdmVersion = WitaCdmVersion.V1;
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder().withId(AKM_PV_ID)
                .withWitaVersion(witaCdmVersion).build();

        Mockito.when(mwfEntityDao.findById(AKM_PV_ID, AnkuendigungsMeldungPv.class)).thenReturn(akmPv);
        // @formatter:off
        Map<String, Object> taskParams = ImmutableMap.<String, Object> of(
                WitaTaskVariables.WITA_MESSAGE_TYPE.id, MeldungsType.AKM_PV.name(),
                WitaTaskVariables.AKM_PV_ID.id, akmPv.getId(),
                WitaTaskVariables.WITA_IN_MWF_ID.id, akmPv.getId(),
                WitaTaskVariables.WORKFLOW_ERROR.id, false);
        // @formatter:on

        ProcessInstance processInstance = commonWorkflowService.newProcessInstance(Workflow.ABGEBEND_PV, AKM_PV_EXTAUFTRAGSNUMMER, taskParams);

        Execution execution = activitiRuntimeService.createExecutionQuery()
                .processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        runtimeService.setVariable(execution.getId(), WitaTaskVariables.RUEM_PV_ANTWORTCODE.id, RuemPvAntwortCode.OK);
        // Activiti calls setValue twice
        verify(ruemPvAntwortCodeVariableTypeSpy, times(2)).setValue(any(Object.class), any(ValueFields.class));

        RuemPvAntwortCode result = (RuemPvAntwortCode) runtimeService.getVariable(execution.getId(),
                WitaTaskVariables.RUEM_PV_ANTWORTCODE.id);
        assertThat(result, equalTo(RuemPvAntwortCode.OK));
        verify(ruemPvAntwortCodeVariableTypeSpy).getValue(any(ValueFields.class));
    }
}
