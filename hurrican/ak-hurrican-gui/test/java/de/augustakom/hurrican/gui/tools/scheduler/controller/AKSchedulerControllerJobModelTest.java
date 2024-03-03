/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2011 09:52:42
 */
package de.augustakom.hurrican.gui.tools.scheduler.controller;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class AKSchedulerControllerJobModelTest extends BaseTest {

    @DataProvider
    public Object[][] fullNames() {
        return new Object[][] {
                { "DEFAULT.test", "DEFAULT", "test" },
                { "DEFAULT.test.group", "DEFAULT", "test.group" },
                { "ANDERE.test.group", "ANDERE", "test.group" },
                { "ANDERE.test.group.mit.vielen.Punkten", "ANDERE", "test.group.mit.vielen.Punkten" },
                { "", "", "" },
                { null, "", "" },
        };
    }

    @Test(dataProvider = "fullNames")
    public void getJobNameAndGroupShouldWork(String fullJobName, String groupName, String jobName) {
        AKSchedulerControllerJobModel job = new AKSchedulerControllerJobModel();
        job.setFullJobName(fullJobName);
        assertEquals(job.getGroupName(), groupName);
        assertEquals(job.getJobName(), jobName);
    }

    @Test(dataProvider = "fullNames")
    public void getTriggerNameAndGroupShouldWork(String fullTriggerName, String groupName, String jobName) {
        AKSchedulerControllerTriggerModel job = new AKSchedulerControllerTriggerModel();
        job.setFullTriggerName(fullTriggerName);
        assertEquals(job.getGroupName(), groupName);
        assertEquals(job.getTriggerName(), jobName);
    }
}


