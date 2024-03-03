/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.2011 14:54:51
 */

package de.augustakom.common.tools.ws;

import java.io.*;
import java.net.*;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnection;

/**
 * Ableitung vom Spring HttpUrlConnectionMessageSender. Die Ableitung kann die Connection Timeout falls gesetzt
 * ueberschreiben.
 */
public class MnetHttpUrlConnectionMessageSender extends HttpComponentsMessageSender {

    /**
     * Injected/configured with Spring
     */
    private Integer connectionTimeout;

    /**
     * Ueberschreibt default Einstellungen durch eigene Einstellungen
     */
    @Override
    public WebServiceConnection createConnection(URI uri) throws IOException {
        WebServiceConnection connection = super.createConnection(uri);

        if (connection != null && connectionTimeout != null && connection instanceof HttpUrlConnection) {
            HttpURLConnection httpURLConnection = ((HttpUrlConnection) connection).getConnection();
            httpURLConnection.setConnectTimeout(connectionTimeout.intValue());
        }
        return connection;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
