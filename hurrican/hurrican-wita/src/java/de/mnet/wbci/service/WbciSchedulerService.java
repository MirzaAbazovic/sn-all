/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.wbci.service;

import java.util.*;

import de.mnet.wbci.model.WbciRequest;

/**
 *
 */
public interface WbciSchedulerService extends WbciService {

    /**
     * Schedules the provided request for sending. If the configuration parameter minutesOnHold is set to 0, the request
     * will be sent immediately.
     *
     * @param request
     * @param <T>
     */
    <T extends WbciRequest> void scheduleRequest(T request);

    /**
     * Finds all scheduled {@link WbciRequest}s (requests which have {@link WbciRequest#sendAfter} parameter in the past
     * and {@link WbciRequest#processedAt} is null)
     *
     * @return List of {@link WbciRequest} Ids
     */
    List<Long> findScheduledWbciRequestIds();

    /**
     * Sends the provided request to AtlasESB. The request should have a sendAfter date in the past and no processedAt
     * defined.
     *
     * @param scheduledRequestId - Wbci Request Id to be sent
     */
    void sendScheduledRequest(Long scheduledRequestId);

}
