/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2015
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Created by glinkjo on 17.08.2015.
 */
public class CallFindOrCreateGeoIdTestAction extends AbstractTestAction {

    private AvailabilityService availabilityService;
    private Long geoId;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public CallFindOrCreateGeoIdTestAction(AvailabilityService availabilityService, Long geoId) {
        this.availabilityService = availabilityService;
        this.geoId = geoId;
    }

    @Override
    public void doExecute(TestContext testContext) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                testContext.setVariable(VariableNames.GEO_ID, geoId);
                availabilityService.findOrCreateGeoId(geoId, null);
            }
        });
    }

}
