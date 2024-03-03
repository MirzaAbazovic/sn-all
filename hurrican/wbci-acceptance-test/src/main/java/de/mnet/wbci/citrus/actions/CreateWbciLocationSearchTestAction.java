/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.mnet.wbci.citrus.ResponseCallback;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.service.WbciLocationService;

/**
 * Test action performs remote service call on wbci location service for searching locations. Remote service call is
 * done in separate thread because of synchronous nature. Test case continues to execute while remote service call is
 * done, waiting for synchronous response. Test action supports response callback so tester can do assertions on remote
 * service call response object.
 *
 *
 */
public class CreateWbciLocationSearchTestAction extends AbstractWbciTestAction {

    private WbciLocationService wbciLocationService;
    private Standort standort;

    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    private ResponseCallback<List<Long>> responseCallback;

    public CreateWbciLocationSearchTestAction(WbciLocationService wbciLocationService, Standort standort) {
        super("createWbciLocationSearch");
        this.wbciLocationService = wbciLocationService;
        this.standort = standort;
    }

    @Override
    public void doExecute(final TestContext context) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Long> geoIds = wbciLocationService.getLocationGeoIds(standort);
                if (responseCallback != null) {
                    responseCallback.doWithResponse(geoIds, context);
                }
            }
        });
    }

    public CreateWbciLocationSearchTestAction withResponseCallback(ResponseCallback<List<Long>> callback) {
        this.responseCallback = callback;
        return this;
    }
}
