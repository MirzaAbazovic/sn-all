/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 18:01:57
 */
package de.mnet.hurrican.reporting.service;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.service.status.ApplicationStatusResult;
import de.mnet.common.service.status.ApplicationStatusService;

/**
 * Prueft die Verbindung zum Open-Office-Server.
 */
public class OpenOfficeConnectionStatus implements ApplicationStatusService {

    private static final Logger LOGGER = Logger.getLogger(OpenOfficeConnectionStatus.class);

    @Autowired
    private OpenOfficeService openOfficeService;

    @Override
    public ApplicationStatusResult getStatus() {
        ApplicationStatusResult status = new ApplicationStatusResult();
        OpenOfficeConnection connection = null;
        try {
            connection = openOfficeService.getConnection();
            if (!connection.isConnected()) {
                status.addError("Not connected to OpenOfficeServer!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            status.addError("Error connecting to OpenOfficeServer: " + e.getMessage());
        }
        return status;
    }

    @Override
    public String getStatusName() {
        return "Connection to OpenOfficeServer";
    }

}


