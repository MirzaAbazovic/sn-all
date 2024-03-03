/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2014
 */
package de.mnet.wbci.citrus.actions;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 *
 */
public class VerifyAutomationTasksAction extends AbstractWbciTestAction {

    private final List<AutomationTask> expectedAutomationTasks;
    private WbciCommonService wbciCommonService;
    private boolean verifyEqualSize;

    /**
     * 
     * @param wbciCommonService
     * @param expectedAutomationTasks
     * @param verifyEqualSize Flag gibt an, ob die Anzahl der gefundenen AutomationTasks mit den erwarteten
     *                        AutomationTasks uebereinstimmen muss (true) oder ob auch mehr AutomationTasks
     *                        vorhanden sein duerfen (false)
     */
    public VerifyAutomationTasksAction(
            WbciCommonService wbciCommonService, 
            List<AutomationTask> expectedAutomationTasks,
            boolean verifyEqualSize) {
        
        super("verifyAutomationTasks");
        this.wbciCommonService = wbciCommonService;
        this.expectedAutomationTasks = expectedAutomationTasks;
        this.verifyEqualSize = verifyEqualSize;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);
        if (vorabstimmungsId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        final List<AutomationTask> automationTasks = wbciGeschaeftsfall.getAutomationTasks();
        
        if (verifyEqualSize) {
            assertEquals(automationTasks.size(), expectedAutomationTasks.size(), "count of found automation tasks failed " + expectedAutomationTasks);
        }
        
        for (AutomationTask expectedAutomationTask : expectedAutomationTasks) {
            assertAutomationTaskInList(automationTasks, expectedAutomationTask);
        }
    }

    private void assertAutomationTaskInList(List<AutomationTask> automationTasks, AutomationTask taskToCheck) {
        for (AutomationTask automationTask : automationTasks) {
            boolean found = automationTask.getStatus().equals(taskToCheck.getStatus())
                    && automationTask.getName().equals(taskToCheck.getName())
                    && automationTask.isDone() == taskToCheck.isDone()
                    && automationTask.getUserName().equals(taskToCheck.getUserName());

            if (StringUtils.isNotEmpty(taskToCheck.getExecutionLog())) {
                found = automationTask.getExecutionLog().contains(taskToCheck.getExecutionLog());
            }

            if (found) {
                return;
            }
        }
        throw new CitrusRuntimeException(String.format("Following AutomationTask could not be found: '%s' %n Instead found '%s'", taskToCheck, CollectionTools.formatCommaSeparated(automationTasks)));
    }

}
