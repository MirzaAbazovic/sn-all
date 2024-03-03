/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.03.2009 13:36:51
 */
package de.mnet.hurrican.webservice.alive.endpoint;

import java.util.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.hurricanweb.alive.types.WSAliveRequest;
import de.mnet.hurricanweb.alive.types.WSAliveRequestDocument;

/**
 * Endpoint-Implementierung, um eine 'Alive'-Pruefung der Web-Applikation durchfuehren zu koennen.
 *
 *
 */
@Endpoint
public class AliveEndpoint {

    @PayloadRoot(localPart = "WSAliveRequest", namespace = "http://mnet.de/hurricanweb/alive/types")
    @ResponsePayload
    public WSAliveRequestDocument aliveRequest(@RequestPayload WSAliveRequestDocument reqDoc) {
        WSAliveRequest req = reqDoc.getWSAliveRequest();
        req.setAlive("I`m alive. Time: " + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL));
        return reqDoc;
    }

}


