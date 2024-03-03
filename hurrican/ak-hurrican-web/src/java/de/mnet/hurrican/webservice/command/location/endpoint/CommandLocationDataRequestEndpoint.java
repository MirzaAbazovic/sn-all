/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2009 11:49:08
 */
package de.mnet.hurrican.webservice.command.location.endpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import de.augustakom.hurrican.model.cc.view.FTTXStandortImportView;
import de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint;
import de.mnet.hurrican.webservice.command.base.CommandException;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequest;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestDocument;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestFailure;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestFailureDocument;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestResponse;
import de.mnet.hurricanweb.command.location.types.CommandLocationDataRequestResponseDocument;

/**
 * Endpoint-Implementierung, um Standort-Daten von Command zu importieren.
 *
 *
 */
public class CommandLocationDataRequestEndpoint extends CommandBaseRequestEndpoint<FTTXStandortImportView> {
    private static final Logger LOGGER = Logger.getLogger(CommandLocationDataRequestEndpoint.class);

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CommandLocationDataRequestEndpoint called");
        return super.invokeInternal(requestObject);
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#execute(Object)
     */
    @Override
    protected void execute(FTTXStandortImportView model) throws Exception {
        // Erzeuge Standort
        if ((model != null)) {
            fttxHardwareService.generateFTTXStandort(model, getSessionId());
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateResponseMessage(java.lang.String,
     * Object)
     */
    @Override
    protected Object generateResponseMessage(String responseString, FTTXStandortImportView model) throws Exception {
        CommandLocationDataRequestResponseDocument responseDocument = CommandLocationDataRequestResponseDocument.Factory.newInstance();
        CommandLocationDataRequestResponse response = responseDocument.addNewCommandLocationDataRequestResponse();
        response.setBezeichnung(model.getBezeichnung());
        return response;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#generateFaultMessage(java.lang.Exception,
     * Object)
     */
    @Override
    protected XmlObject generateFaultMessage(Exception e, FTTXStandortImportView model) throws CommandException {
        CommandLocationDataRequestFailureDocument failureDocument = CommandLocationDataRequestFailureDocument.Factory.newInstance();
        CommandLocationDataRequestFailure failure = failureDocument.addNewCommandLocationDataRequestFailure();
        failure.setBezeichnung(model.getBezeichnung());
        failure.setErrorMsg(e.getMessage());

        return failureDocument;
    }

    /**
     * @see de.mnet.hurrican.webservice.command.base.CommandBaseRequestEndpoint#mapRequestToModel(java.lang.Object)
     */
    @Override
    protected FTTXStandortImportView mapRequestToModel(Object requestObject) throws Exception {
        CommandLocationDataRequestDocument document = (CommandLocationDataRequestDocument) requestObject;
        CommandLocationDataRequest data = document.getCommandLocationDataRequest();

        FTTXStandortImportView model = new FTTXStandortImportView();
        model.setBezeichnung(StringUtils.trimToNull(data.getBezeichnung()));
        model.setGeoId(Long.valueOf(data.getGeoId()));
        model.setStrasse(StringUtils.trimToNull(data.getStrasse()));
        model.setHausnummer(StringUtils.trimToNull(data.getHausnummer()));
        model.setHausnummerZusatz(StringUtils.trimToNull(data.getHausnummerZusatz()));
        model.setPlz(StringUtils.trimToNull(data.getPlz()));
        model.setOrt(StringUtils.trimToNull(data.getOrt()));
        model.setNiederlassung(StringUtils.trimToNull(data.getNiederlassung()));
        model.setBetriebsraum(StringUtils.trimToNull(data.getBetriebsraum()));
        model.setFcRaum(StringUtils.trimToNull(data.getFcRaum()));
        model.setStandortTyp(StringUtils.trimToNull(data.getStandorttyp()));
        model.setOnkz(StringUtils.trimToNull(data.getOnkz()));
        model.setAsb(Integer.valueOf(data.getAsb().trim()));
        model.setVersorger(StringUtils.trimToNull(data.getVersorger()));
        model.setClusterId(StringUtils.trimToNull(data.getClusterId()));

        return model;
    }
}
