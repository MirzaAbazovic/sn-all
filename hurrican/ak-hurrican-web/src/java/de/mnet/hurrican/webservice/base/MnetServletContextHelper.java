/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2009 12:08:49
 */
package de.mnet.hurrican.webservice.base;

import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;


/**
 * Utils-Klasse fuer die Arbeit mit ServletContext.
 *
 *
 */
public class MnetServletContextHelper {

    /**
     * Ermittelt den aktuellen ServletContext ueber den TransportContextHolder.
     *
     * @return der aktuelle ServletContext
     *
     */
    public static ServletContext getServletContextFromTCH() {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection) context.getConnection();
        HttpServletRequest request = connection.getHttpServletRequest();
        return request.getSession().getServletContext();
    }

}


