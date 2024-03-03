/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2010
 */
package de.mnet.hurrican.reporting.service.impl;

import java.net.*;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import org.apache.log4j.Logger;

import de.mnet.hurrican.reporting.service.OpenOfficeService;

public class OpenOfficeServiceImpl implements OpenOfficeService {

    private static final Logger LOGGER = Logger.getLogger(OpenOfficeServiceImpl.class);

    private int port;
    private String host;
    private OpenOfficeConnection connection;

    public OpenOfficeServiceImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public synchronized OpenOfficeConnection getConnection() {
        if (connection != null && connection.isConnected()) {
            return connection;
        }
        else {
            connection = new SocketOpenOfficeConnection(host, port);
            try {
                connection.connect();
                LOGGER.debug("getConnection() - Neue Verbindung zu OpenOffice hergestellt.");
                return connection;
            }
            catch (ConnectException ex) {
                LOGGER.error("getConnection() - Verbindung zu OpenOffice fehlgeschlagen: " + ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public DocumentConverter getDocumentConverter() {
        return new StreamOpenOfficeDocumentConverter(getConnection());
    }

}
