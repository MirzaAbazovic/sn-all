/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.command.base;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.FTTXInfoService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;

/**
 * Basis Endpoint, um von Command zu importieren.
 *
 *
 */
public abstract class CommandBaseRequestEndpoint<MODEL> extends MnetAbstractMarshallingPayloadEndpoint {
    private static final Logger LOGGER = Logger.getLogger(CommandBaseRequestEndpoint.class);

    protected FTTXHardwareService fttxHardwareService = null;
    protected FTTXInfoService fttxInfoService = null;

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        initService();
        MODEL model = mapRequestToModel(requestObject);

        try {
            execute(model);
            return generateResponseMessage(null, model);
        }
        catch (Exception e) {
            throw new CommandException(e.getMessage(), e, generateFaultMessage(e, model));
        }
    }

    /**
     * Initialisiert die Services
     */
    protected void initService() throws Exception {
        try {
            fttxHardwareService = getCCService(FTTXHardwareService.class);
            fttxInfoService = getCCService(FTTXInfoService.class);
        }
        catch (Exception e) {
            LOGGER.error("Could not initialize services", e);
            throw e;
        }
    }


    /**
     * Erzeugt Response im Fehler-Fall über eine Exception
     */
    protected abstract XmlObject generateFaultMessage(Exception e, MODEL model) throws CommandException;

    /**
     * Erzeugt Response
     */
    protected abstract Object generateResponseMessage(String responseString, MODEL model) throws Exception;

    /**
     * Schreibt Daten vom Request in das Modell
     */
    protected abstract MODEL mapRequestToModel(Object requestObject) throws Exception;

    /**
     * Führt den jeweiligen Import aus
     */
    protected abstract void execute(MODEL model) throws Exception;
}
