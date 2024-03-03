/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 13:37:11
 */
package de.mnet.wita.bpm.test;

import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.exceptions.WitaBpmException;

/**
 * Base class for Activiti unit tests.
 * <p/>
 * By default, you should use a {@link ContextConfiguration} annotation to setup a Spring Context. Then the base Test
 * will use an autowired bean of type {@link ActivitiTestNGRule} to setup activity. If you want to change this
 * behaviour, you need to overwrite the method setActivitiTestNgRule
 */
@TransactionConfiguration(transactionManager = "cc.hibernateTxManager")
public abstract class AbstractActivitiBaseTest extends AbstractTransactionalTestNGSpringContextTests {

    private static final Logger LOGGER = Logger.getLogger(AbstractActivitiBaseTest.class);

    private ActivitiTestNGRule activitiTestNgRule;

    @Autowired
    protected HistoryService activitiHistoryService;
    @Autowired
    protected RuntimeService activitiRuntimeService;

    // Real service through activiti-test-context
    @Autowired
    protected CommonWorkflowService commonWorkflowService;

    @BeforeSuite(alwaysRun = true)
    public final void setupLogging() {
        InitializeLog4J.initializeLog4J("log4j-test");
    }

    @AfterMethod(alwaysRun = true)
    public final void finished(Method method) {
        if (activitiTestNgRule != null) {
            activitiTestNgRule.finished(method);
            activitiTestNgRule.getProcessEngine().close();
        }

        if (activitiRuntimeService != null) {
            List<ProcessInstance> instances = activitiRuntimeService.createProcessInstanceQuery()
                    .list();
            for (ProcessInstance processInstance : instances) {
                try {
                    activitiRuntimeService.deleteProcessInstance(processInstance.getId(), null);
                }
                catch (Exception e) {
                    LOGGER.warn("Error deleting process with Id " + processInstance.getId(), e);
                }
            }
        }

        if (activitiHistoryService != null) {
            List<HistoricProcessInstance> historicInstances = activitiHistoryService
                    .createHistoricProcessInstanceQuery()
                    .list();
            for (HistoricProcessInstance historicProcessInstance : historicInstances) {
                activitiHistoryService.deleteHistoricProcessInstance(historicProcessInstance.getId());
            }
        }
    }

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestMethod")
    public final void starting(Method method) {
        if (activitiTestNgRule != null) {
            activitiTestNgRule.starting(method);
        }
    }

    @Autowired
    public void setActivitiTestNgRule(ActivitiTestNGRule activitiTestNgRule) {
        this.activitiTestNgRule = activitiTestNgRule;
    }

    public final ActivitiTestNGRule getActivitiTestNgRule() {
        return activitiTestNgRule;
    }

    /**
     * Helper methods for shorter test code especially within data providers
     */
    protected <T, V> Pair<T, V> pair(T first, V second) {
        return Pair.create(first, second);
    }

    protected void assertProcessInstanceInState(ProcessInstance pi, WorkflowTaskName state, String expectedBusinesskey) {
        assertProcessInstanceIsFinished(pi, false, expectedBusinesskey);

        assertNotNull(pi);
        assertTrue(pi instanceof ExecutionEntity);
        assertEquals(((ExecutionEntity) pi).getActivityId(), state.id);

        HistoricProcessInstance hpi = activitiHistoryService
                .createHistoricProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNull(hpi.getEndTime());
    }

    protected void assertProcessInstanceIsFinished(CanOpenActivitiWorkflow message) {
        String expectedBusinesskey = message.getBusinessKey();
        ProcessInstance pi;
        try {
            pi = commonWorkflowService.retrieveProcessInstance(message.getBusinessKey());
        }
        catch (WitaBpmException e) {
            pi = null;
        }
        assertProcessInstanceIsFinished(pi, true, expectedBusinesskey);
    }

    protected void assertProcessInstanceIsFinished(ProcessInstance pi, boolean expectedToBeFinished,
            String expectedBusinesskey) {
        if (expectedToBeFinished) {
            assertNull(pi, "ProcessInstance found but expected to be not found (because finished)");
        }
        else {
            assertNotNull(pi, "ProcessInstance not found but expected to be found");
        }

        HistoricProcessInstance hpi = activitiHistoryService
                .createHistoricProcessInstanceQuery().processInstanceBusinessKey(expectedBusinesskey).singleResult();
        assertEquals(hpi.getBusinessKey(), expectedBusinesskey);
        if (expectedToBeFinished) {
            assertNotNull(hpi.getEndTime());
        }
        else {
            assertNull(hpi.getEndTime());
        }
    }

}
