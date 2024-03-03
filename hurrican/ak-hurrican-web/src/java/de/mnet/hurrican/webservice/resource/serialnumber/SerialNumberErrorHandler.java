/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2015
 */
package de.mnet.hurrican.webservice.resource.serialnumber;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.errorlog.ErrorLogDAO;
import de.augustakom.hurrican.model.cc.ErrorLogEntry;
import de.augustakom.hurrican.service.cc.ICCService;

/**
 *
 */
@CcTxRequiresNew
@Service
public class SerialNumberErrorHandler implements ICCService {

    private static final Logger LOGGER = Logger.getLogger(SerialNumberErrorHandler.class);
    private static final String SERVICENAME = "SerialNumberFFMService";

    @Autowired
    protected ErrorLogDAO errorLogDAO;

    /**
     * Writes error log table entry
     * @param resourceId
     * @param serialnumber
     * @param errorName
     * @param errorDescription
     */
    public void storeErrorLogEntry(String resourceId, String serialnumber, String errorName, String errorDescription, String stacktrace) {
        String errorDesc = (StringUtils.isNotEmpty(resourceId) ? "resourceId: " + resourceId + " \n" : "")
                + (StringUtils.isNotEmpty(serialnumber) ? "serialNumber: " + serialnumber + " \n" : "")
                + (StringUtils.isNotEmpty(errorDescription) ? "message: " + errorDescription : "");
        final ErrorLogEntry logEntry = new ErrorLogEntry(errorName, errorDesc, SERVICENAME, stacktrace);
        errorLogDAO.store(logEntry);
    }

}
